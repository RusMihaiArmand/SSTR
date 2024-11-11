# Tema 2

Instructiuni:

Se porneste RabbitMQ.
Se pornesc Controller, Actuator si Logger.
Se pornesc SensorTemperature si SensorHumidity, sau variantele Manual pentru o testare manuala a
aplicatiei.

Senzorii vor trimite valorile citite spre Controller la un interval de 5 secunde.
Senzorul de temperatura va da temperaturi in intervalul 10.0 - 60.0, numere cu cel mult o zecimala.
Senzorul de umiditate va da valori in intervalul 25-55.

Controllerul va primi valorile si le va compara cu valorile de referinta - 35 de grade pentru temperatura,
respectiv 40% pentru umiditate.

Mesajul generat de Controller depinde de diferenta dintre valoarea primita si valoarea de referinta:
1)pentru temperatura:
> o diferenta de maxim 2 grade (pe plus sau pe minus) va genera comanda de oprire a incalzirii si racirii
> o diferenta de 2-15 grade va genera comanda de a porni incalzirea/racirea
> o diferenta de 15+ grade va genera comanda de a porni si incalzirea/racirea secundara

2)pentru umiditate:
> o diferenta de maxim 3% va genera comanda de oprire a umidificator si dezumidificator
> o diferenta de 3-10% va genera comanda de a porni umidificatorul/dezumidificatorul
> o diferenta de 10+% va genera comanda de a porni si umidificatorul/dezumidificatorul secundar

Actuatorul primeste comanda si o executa.

Loggerul primeste aceeasi comanda si o salveaza in fisierul data.json; el va salva comanda si 
momentul generarii acesteia.
