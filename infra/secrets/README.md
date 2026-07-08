# infra/secrets/

Esta carpeta está en `.gitignore` — nunca se commitean secretos reales al repositorio.

`bootstrap-swarm.sh` genera aquí, de forma local, el par de llaves RS256 para JWT
(`jwt_private_key.pem` / `jwt_public_key.pem`) y crea el resto de los secretos
(`postgres_password`, `rabbitmq_password`) directamente en el almacén cifrado de
Docker Swarm — esos dos últimos nunca tocan el disco como archivo.

Para generar tus propios secretos y desplegar el stack completo:

```bash
chmod +x infra/bootstrap-swarm.sh
./infra/bootstrap-swarm.sh

docker stack deploy -c infra/docker-stack.yml peribook
```

Ver la sección 2.4 del plan de arquitectura (`PeriBook-Plan-Arquitectura.md`) para el
detalle completo de por qué se usa Docker Swarm y `docker secret` en vez de variables
de entorno planas.
