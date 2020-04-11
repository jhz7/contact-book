# Contact Book

## El problema:
Se requiere tener un directorio de contactos de personas naturales con las cuales poder hacer negocios en el futuro.  
El criterio para agregar un contacto al directorio es que pase una serie de validaciones que nos permitan estar seguros 
de que es una persona confiable y apta para nuestro negocio.  
Inicialmente la persona natural es agregada a un directorio temporal de prospectos donde especificamos sus datos 
personales (tipo de identificación, número de documento, fecha de expedicón, etc). Luego, para que la persona pueda ingresar 
al directorio oficial de contactos, ejecutamos un proceso de validación que aplica tres criterios:  

* La persona existe en el sistema de identificación personal de la república y sus datos personales coiciden con la respuesta 
dada por el sistema externo.
* La persona no tiene antecedentes judiciales al hacer la integración con el sistema de la policía de la república.
* La persona tiene un puntaje satisfactorio según nuestro sistema de calificación de prospectos.  
 
 Las dos primeras validaciones no tienen dependencia entre ellas y se espera que se ejecuten en paralelo, para ejecutar 
 la tercera validación, se espera contar con la respuesta de las dos primeras validaciones para poder enviar la petición 
 a nuestro sistema de calificación de prospectos.  
 Si la última validación es exitosa, el propecto será agregado a nuestro directorio de contactos y se debe imprimir en 
 consola un mensaje satisfactorio. De lo contrario, se debe imprimir por consola un mensaje de error.  
 
 Consideraciones:
 * Los sistema externos deben ser implementados como una función que responde exitosamente o con errores dependiendo de 
 la persona a consultar.  
 Simule latencia en los llamados entre nuestro sistema y éstos sistemas externos.  
 Los sistemas externos son una implementación ficticia  que se puede hacer con un stub http. Sin embargo, usted es libre 
 de usar la técnica que considere necesaria para simular éstos sistemas.
 * El sistema de calificación de prospectos es simplemente una función que responde con un puntaje aleatorio entre 0 y 100.
 Un prospecto puede ser agregado al directorio de contactos con un puntaje superior o igual a 60 pts.
 * Preferiblememte desarrolle la prueba con un lenguaje que corra sobre la JVM, puede usar paradigma funcional u OOP con 
 las librerías que considere necesarias.  
 * Tenga en cuenta que se espera validar su capacidad de estructurar una buena solución al problema, y no necesariamente 
 su capacidad de desarrollar algoritmos.
 * Si, se espera que existan pruebas unitarias que prueben la correctitud de la solución desarrollada en varios escenarios.
 * No, se espera que desarrolle una interfaz de usuario o que la aplicación sea clente servidor.
 * No, se espera que haga uso de componentes de infraestructura como bases de datos o brokers de mensajería.
