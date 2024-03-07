# Lo primero que vamos a crear sera la estructura de carpeta de una vez
- application
- domain
- infrastructure


En la capa de domain, crearemos 2 carpetas:
- model
- exception

En la capa de application, crearemos 2 carpetas:
- ports -> Dentro de esto 2 carpetas mas input y output
- services

En la capa de Infrastructure, crearemos 2 carpetas:
- adapters -> Dentro creamos input y output
en input de adapters estaran los controladores rest y en output estaran la comunicacion con la base de datos
