# kubo
Examen Práctico TI SR

El algoritmo para elegir la mejor coincidencia de un nombre dado un patrón de búsqueda consiste
primeramente en separar el nombre completo en nombre y apellidos. Ejemplo:

Juan Pérez Jolote -> [Juan,Pérez,Jolote]

Se trabaja sobre este array para determinar la distancia del nombre completo palabra por palabra,
es decir, se calcula la distancia del nombre(s) y apellido(s) y la mejor se considera la distancia
de la palabra.

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

