# recordar
- Los mapper de persistencia son los encargados de convertir los modelos de dominio a entidades y viceversa.
- Ademas necesitamos instalar el plugin de mapstruct en nuestro proyecto.

# Vamos a crear los mapper de persistencia de cada entidad (interfaces)
- UserPersistenceMapper


Una vez creado los mapper, nos dirigimos al primer archivo que creamos en infraestructure adapters, output, persistence que es UserPersistenceAdapter
entonces, debemos inyectar la dependencia del Repository y ademas del Mapper que creamos


# Una vez hecho el USerPersistenceAdapter, nos vamos a la parte de input de la misma carpeta de infraestructure-> adapter -> input