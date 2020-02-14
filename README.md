# kubo
Examen Práctico TI SR

El algoritmo para elegir la mejor coincidencia de un nombre dado un patrón de búsqueda consiste
primeramente en separar el nombre completo en nombre y apellidos. Ejemplo:

Juan Pérez Jolote -> [Juan,Pérez,Jolote]

Se trabaja sobre este array para determinar la distancia del nombre completo palabra por palabra,
es decir, se calcula la distancia del nombre(s) y apellido(s) y la mejor se considera la distancia
de la palabra.

La distancia de edición se cálcula con el algoritmo de Levenshtein, esta distancia indica el número
mínimo de operaciones para transformar una cadena en otra, es un algoritmo que existe desde 1965
y consiste en generar una matriz de (lenth(string1)+1) * (lenth(string2)+1). Se considera que 
cada operación de mutación (Eliminación, Iserción y sustitución) tiene un valor unitario, 
la matriz cuantifica el número de operaciones a realizar para transformar un string en otra.
Una explicación sencilla del método se puede encontrar en el siguiente enlace:
https://www.youtube.com/watch?v=MiqoA-yF-0M


Una vez calculada la distancia del primer nombre se calcula la distancia del siguiente,
si la distancia del siguiente nombre es mejor, se actualiza el indice de mejor distancia.

Cuando se termina de calcular la distancia de Levenshtein de todos los nombres registrados se
verifica que la menor distancia no sea mayor a 3, claramente esta variable de "sensibilidad" 
puede ser ajustada según se desee.

Se eligió usar este algoritmo por la combinación de simplicidad y eficacia. Aunque Boyer Moore 
es un algoritmo con mayor eficiencia está orientado a la coincidencia exacta de un string dentro 
de otro. Es un problema interesante ya que la mayoría de las veces vemos el resultado de este
algoritmo en frameworks de frontend y motores de bases de datos pero conocer la implementación y
la historia de este es bastante interesante.

El programa se escribió en Java por comodidad.

Para compilar usar: javac Fuzzy.java
Para ejecutar los comandos usar: java Fuzzy comando argumento

*** Nota:
Para procesar los JSON no se utilizó ninguna libreria externa por lo que la función es un tanto 
precaria, por favor envía los json entre apostrofes (') y no incluyas espacios dentro de la estructura
JSON, unicamente dentro del valor name y el valor search. Ejemplo.

'{"search":"Alverar"}' ->OK
'{ "search" :"Alverar"}' - BAD
'{"name":"Arturo Nieves"}' ->  OK
'{"name": "Arturo Nieves" }' -> BAD

De cualquier manera el programa te indicara que estás ingresando los datos en un formato inadecuado.



