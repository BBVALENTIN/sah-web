# Contextul actual

Șahul este un joc de strategie pentru doi jucători, pe o tablă de 64 de pătrate, dispuse pe o grilă 8x8. Fiecare jucător controlează un set de 16 piese, fiecare tip de piesă având reguli diferite de deplasare, obiectivul jocului fiind de a da „mat”, ceea ce reprezintă amenințarea regelui cu o captură imposibil de evitat. 

Originar din India, în jurul secolului al VI-lea, șahul a evoluat de-a lungul timpului, atât ca reguli, deoarece șahul a suferit diverse schimbări în parcursul acestuia, ajungând la forma pe care o știm astăzi în jurul anilor 1850, dar și ca popularitate. 

Odată cu dezvoltarea tehnologiilor web, șahul a fost adaptat mediului digital, permițând jucătorilor să interacționeze în timp real indiferent de locație. Platformele online de șah oferă funcționalități diverse, precum analizarea automată a partidelor, medii de învățare ș.a.m.d. 

În acest context, proiectul de față își propune dezvoltarea unei aplicații web de șah, utilizând frameworkul specific Java „Spring Boot”, care să permită gestionarea jocurilor de șah în timp real între utilizatori. 

# Sursa de idei și motivația

Una dintre pasiunile mele a fost șahul, iar ideea de a dezvolta o aplicație web a venit încă din primul an de facultate, când în cadrul laboratorului de „Programare Orientată pe Obiect”, am dezvoltat o aplicație de șah de tip single-player, cu interfață grafică, folosind Java. 

Scopul proiectului constă în dezvoltarea unei aplicații web interactive de șah, care să permită utilizatorilor să joace șah în timp real, dar și să îi ajute să devină mai buni la jocul de șah datorită diferitor funcționalități ale aplicației. Lucrarea este de tip aplicativ, având ca scop dezvoltarea unei aplicații web funcționale pentru jocul de șah în timp real. 

Aplicația urmărește implementarea unui mecanism eficient de gestionare a stării jocului, precum și sincronizarea mișcărilor între doi jucători folosind protocolul de comunicare „WebSocket”, astfel încât experiența de joc să fie una fluentă și coerentă. De asemenea, proiectul își propune diferite funcționalități pentru a deservi și ca mediu de învățare pentru cei noi în jocul de șah.

# Cercetare

Înainte de proiectarea proiectul, a fost necesară o cercetare a jocului de șah, dar și a aplicațiilor deja existente. O sursă foarte bună a fost site-ul lichess.org, deoarece acesta este o aplicație open-source și poate fi foarte ușor accesată pe github.com/lila., cu o documentație foarte ușor de urmărit. Scopul acestei cercetări a fost familiarizarea cu tehnologiile de comunicare dintre server și interfața utilizatorului. ``CONTINUE maybe?

# Licențiere

Din cauza faptului că proiectul folosește fișiere audio provenite din proiectul Lichess.org (github.com/lila), folosite în aplicarea mutărilor într-un meci de șah. Proiectul este licențiat sub GNU Affero General Public License versiunea 3 (AGPL v3), iar aceasta permite utilizarea, studiere, modificare și redistrbuire cu condiția publicării codului sursă la rândul nostru.

De asemenea, aplicația utilizează și o bază de date GeoLite2 de la MaxMind pentru determinarea țării utlizatorului asociată pe adresa IP (folosită numai la înregistrarea contului, țara putând fi schimbată ulterior). Datele sunt furnizate de MaxMind și sunt utilizate conform termenilor și condițiilor impuse de aceștia ("End User License Agreement" - EULA).

