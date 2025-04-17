# ENDPOINTS APP-GESTMON

Para sacar SUIP, presiona la tecla windows + R y escribe cmd, una vez alli escribe ipconfig y tomas la direccion IPv4

### LOGIN

```powershell
curl --location 'http://SUIP:8000/api/v1/usuario/login' \
--header 'Content-Type: application/json' \
--data '{
           "codigoEstudiante": "JP12345",
           "clave": "password123"
         }'
```

Aqui se debe enviar codigoEstudiante y clave para que el endpoint vaya contra la base de datos y realice su validacioon

## REGISTRO

```shell
curl --location 'http://SUIP:8000/api/v1/usuario/register' \
--header 'Content-Type: application/json' \
--data-raw '{
           "nombre": "Juan",
           "apellido": "Pérez",
           "email": "juan.perez@example.com",
           "rol": "Estudiante",
           "codigoEstudiante": "JP12345",
           "clave": "password123"
         }'
```

Aqui se debe enviar nombre,apellido,email,rol,codigoEstudiante y clave para que el endpoint valide los datos y posteriormente haga su registro.
