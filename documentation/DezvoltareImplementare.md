# Importanța versionării

Pentru acest proiect am decis să folosesc „git”, un sistem open-source de versionare distribuit, care are ca scop 
gestionarea proiectelor. Acesta ajută prin crearea unor puncte de salvare al modificărilor. De asemenea, se permite și 
salvarea acestor puncte pe un server extern cum ar fi „GitHub” sau „GitLab”, toate acestea folosind interfața git.
Pentru învățarea comenzilor, am consultat manualul de documentație oficial git.

# Tehnologii folosite și căi de comunicare

Pentru realizarea părții de server a proiectului, am folosit pachetul de dezvoltare „Java Development Kit” 17 (JDK 17). Am ales acest 
pachet de dezvoltare din cauza faptului că este un pachet stabil și pentru că am dezvoltat și alte proiecte cu ajutorul
acestuia. Pentru realizarea părții de interfață a utilizatorului am decis să folosesc inițial HTML/CSS/JavaScript, ulterior
am schimbat JavaScript în detrimentul lui TypeScript. Schimbarea s-a datorat faptului că TypeScript este un limbaj
care adaugă variabile statice, în locul celor dinamice din JavaScript. Acest lucru este foarte important baza de cod
din interfață devine foarte mare, fiind ușor să pierzi cursul programului.

Pentru comunicarea dintre server și interfața utilizatorului s-au folosit diferite protocoale de comunicare, cum ar fi:

    - Websocket
    - HTTP

Ambele sunt protocoale la nivel de aplicație, care rulează peste TCP, iar la nivel de rețea, fiind o aplicație web avem
protocolul IP.
<figura explicativă model ISO/OSI aplicație, sau descrisă o ierarhie cu toate protocoalele folosite explicate>

# Segmente importante

Unul dintre cele mai importante segmente, și punctul cheie al aplicației este logica jocului de șah. În jurul 
acestuia sunt realizate toate celelalte utilități ale site-ului. Logica este împărțită în multe clase, 

# Dificultăți întâmpinate