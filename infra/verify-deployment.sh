#!/usr/bin/env bash
# ==============================================================================
# PeriBook — verify-deployment.sh
#
# Valida que todos los servicios del stack estén en 1/1 y que los endpoints
# principales respondan correctamente.
#
# Uso:
#   chmod +x infra/verify-deployment.sh
#   ./infra/verify-deployment.sh
# ==============================================================================

set -euo pipefail

GATEWAY="${GATEWAY:-http://localhost:8080}"
PASS=0
FAIL=0

green() { printf '\033[0;32m  ✓\033[0m %s\n' "$1"; ((PASS++)); }
red()   { printf '\033[0;31m  ✗\033[0m %s\n' "$1"; ((FAIL++)); }

echo "============================================="
echo " PeriBook — Verificación de despliegue"
echo " Gateway: $GATEWAY"
echo "============================================="
echo ""

# ── 1. Verificar réplicas ────────────────────────────────
echo "[1/5] Verificando réplicas del stack..."
EXPECTED_SERVICES=9
RUNNING=$(docker stack services peribook --format '{{.Replicas}}' 2>/dev/null | grep -c "1/1" || true)

if [ "$RUNNING" -ge "$EXPECTED_SERVICES" ]; then
  green "Todas las réplicas en 1/1 ($RUNNING/$EXPECTED_SERVICES servicios)"
else
  red "$RUNNING/$EXPECTED_SERVICES servicios en 1/1 — revisa 'docker stack services peribook'"
fi

docker stack services peribook 2>/dev/null || red "No se pudo obtener el estado del stack"
echo ""

# ── 2. Health check ─────────────────────────────────────
echo "[2/5] Verificando health endpoints..."

check_health() {
  local name="$1" url="$2"
  if curl -sf --max-time 5 "$url" >/dev/null 2>&1; then
    green "$name → $url"
  else
    red "$name → $url no responde"
  fi
}

check_health "Gateway"     "$GATEWAY/actuator/health"
echo ""

# ── 3. Login flow ───────────────────────────────────────
echo "[3/5] Verificando flujo de login..."

LOGIN_RESP=$(curl -s --max-time 10 -X POST "$GATEWAY/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"ana@peribook.com","password":"secreto123"}') || true

if echo "$LOGIN_RESP" | grep -q '"token"'; then
  TOKEN=$(echo "$LOGIN_RESP" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
  green "Login exitoso — token JWT obtenido"
else
  red "Login fallido: $LOGIN_RESP"
  TOKEN=""
fi
echo ""

# ── 4. API endpoints ────────────────────────────────────
echo "[4/5] Verificando endpoints protegidos..."

if [ -n "$TOKEN" ]; then
  check_api() {
    local name="$1" url="$2"
    local code
    code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 \
      -H "Authorization: Bearer $TOKEN" "$url") || code="000"
    if [ "$code" -lt 400 ]; then
      green "$name → $code"
    else
      red "$name → $code ($url)"
    fi
  }

  check_api "GET /bff/feed"   "$GATEWAY/bff/feed"
  check_api "GET /api/posts"  "$GATEWAY/api/posts"
  check_api "GET /api/likes"  "$GATEWAY/api/likes"
else
  red "Sin token JWT — saltando endpoints protegidos"
fi
echo ""

# ── 5. Swagger UI ───────────────────────────────────────
echo "[5/5] Verificando Swagger UI..."
SWAGGER_CODE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "$GATEWAY/docs" || echo "000")
if [ "$SWAGGER_CODE" = "200" ] || [ "$SWAGGER_CODE" = "302" ]; then
  green "Swagger UI accesible en $GATEWAY/docs ($SWAGGER_CODE)"
else
  red "Swagger UI en $GATEWAY/docs → $SWAGGER_CODE"
fi

# ── Resumen ─────────────────────────────────────────────
echo ""
echo "============================================="
echo " Resultado: $PASS pasaron, $FAIL fallaron"
echo "============================================="

if [ "$FAIL" -gt 0 ]; then
  echo ""
  echo "Para debuggear:"
  echo "  docker stack services peribook"
  echo "  docker service logs peribook_auth-service"
  echo "  docker service logs peribook_api-gateway"
  exit 1
else
  echo ""
  echo "✅ El flujo completo (login → feed → API) funciona contra el stack Swarm."
fi
