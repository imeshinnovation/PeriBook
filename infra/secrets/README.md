# infra/secrets/

Esta carpeta está en `.gitignore` — nunca se commitean secretos reales al repositorio.

`bootstrap-swarm.sh` genera aquí, de forma local, el par de llaves RS256 para JWT
(`jwt_private_key.pem` / `jwt_public_key.pem`) y crea el resto de los secretos
(`postgres_password`, `rabbitmq_password`) directamente en el almacén cifrado de
Docker Swarm — esos dos últimos nunca tocan el disco como archivo.

Para generar tus propios secretos y desplegar el stack completo:

```bash
# Opción 1: Script completo (recomendado)
./infra/deploy.sh

# Opción 2: Paso a paso
chmod +x infra/bootstrap-swarm.sh
./infra/bootstrap-swarm.sh

# Construir imágenes
for svc in auth-service user-service post-service like-service \
           realtime-service bff-web api-gateway frontend; do
  docker build -t "peribook/$svc:1.0.0" "$svc/"
done

docker stack deploy -c infra/docker-stack.yml peribook

# Verificar despliegue
./infra/verify-deployment.sh
```

Ver la sección 2.4 del plan de arquitectura (`PeriBook-Plan-Arquitectura.md`) para el
detalle completo de por qué se usa Docker Swarm y `docker secret` en vez de variables
de entorno planas.
<!-- 2026-07-09 -->
