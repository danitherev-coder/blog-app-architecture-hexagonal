# UNa vez creado nuestro modelo, seguimos con el puerto de entrada y luego de salida
Los puertos son INTERFACES que se usaran para la comunicacion con el mundo exterior y el dominio
- Es decir vamos a detallar la funcionalidad que va a exponer la aplicacion, es decir el CRUD xd

## Puerto de entrada(input)
Se le da el nombre del Modelo seguido de Service  + Port
- Seria asi: UserServicePort(interface)

IMPORTANTE:
Los puertos o mejor dicho la capa de Application, solo debe tener dependencia del DOMAIN, es decir, al crear el puerto de entrada(input) o salida(output) debo poner solo el modelo de domain, por ejemplo, los DTOS y Entity se crean en la capa de infraestructura, pero no se deben usar en la capa de application, ya que la capa de application solo debe tener dependencia del domain, es decir, solo debe tener dependencia de los modelos de domain, no de los modelos de infraestructura, por lo que el domain y application debe ser simple.
