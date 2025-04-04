DOKUMENTACIJA

Ovo je backend aplikacija za trgovanje (order matching system) razvijena u Java 21 sa Spring Boot-om, R2DBC (Reactive Relational Database Connectivity) i PostgreSQL. Aplikacija omogućava dodavanje, pronalazenje top 10 naloga i povezivanje naloga za kupovinu i prodaju koristeći WebSocket i REST API.
Tehnologije i alati
•	Java 21
•	Spring Boot
•	R2DBC (za reaktivnu bazu podataka)
•	PostgreSQL
•	Maven
•	WebSocket
•	Reactor (Mono/Flux) za reaktivno programiranje
Konfiguracija baze podataka
Aplikacija koristi PostgreSQL sa R2DBC. Konfiguracija baze se nalazi u application.yml:
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/postgres
    username: postgres
    password: admin
  sql:
    init:
      mode: always



 REST API Endpoints
Za testiranje REST API metoda korišćen je POSTMAN
 1. Dodavanje novog naloga
Endpoint:
POST http://localhost:8080/orders 
Body: JSON format
{
  "amount": 5,
  "price": 25,
  "type": "SELL"
}
Response: HTTP STATUS CODE : 200 ok
{
    "id": 33,
    "amount": 5,
    "price": 25.0,
    "type": "SELL"
}

2. Dohvatanje top 10 BUY naloga 
Endpoint:
GET http://localhost:8080/orders/top-buy
Response: STATUS CODE 200 OK
[
    {
        "id": 19,
        "amount": 5,
        "price": 135567.0,
        "type": "BUY"
    },
    {
        "id": 20,
        "amount": 5,
        "price": 135567.0,
        "type": "BUY"
    },
    {
        "id": 24,
        "amount": 5,
        "price": 1325.0,
        "type": "BUY"
    }.........
PRIMER IZ POSTMANA : 
 
3. Dohvatanje top 10 SELL naloga
Endpoint:
GET http://localhost:8080/orders/top-sell
Response: STATUS CODE 200 OK
[
    {
        "id": 33,
        "amount": 5,
        "price": 25.0,
        "type": "SELL"
    },
    {
        "id": 5,
        "amount": 200,
        "price": 45.0,
        "type": "SELL"
    },
    {
        "id": 6,
        "amount": 200,
        "price": 45.0,
        "type": "SELL"
    }.....

 

 WebSocket Endpoints
Za testiranje metoda putem WebSocketa korišćen je POSTMAN i WebSocketKing online alat zbog testiranja više klijenata odjednom.
 4. Slanje novog naloga putem WebSocket-a
Endpoint:
ws://localhost:8080/ws/orders pomocu ovog url-a se konektujemo na webSocket
Message:
{
  "amount": 5,
  "price": 68,
  "type": "BUY"
}

Response:
Matching sell order found. The transaction will be processed.{
  "id": 34,
  "amount": 5,
  "price": 68,
  "type": "BUY"
}
Pri kreiranju naloga putem WebSocketa klijentu ce stići odgovor kao potvrda kreiranog naloga i obaveštenje svim klijentima o kreiranom nalogu (metoda sendNewOrderUpdates). Takodje u kodu postoji provera orderMatching koja proverava da li postoji odgovarajući nalog suprotnog tipa BUY/SELL, ukoliko postoji servis trenutno samo obaveštava klijenta porukom da u bazi postoji odgovarajući nalog. Ukoliko ne postoji stići ce samo potvrda o kreiranju naloga. U oba slučaja ce nalog biti sačuvan u bazi.
PRIMER IZ WEBSOCKETKING-a :
 
Takodje vidimo da i Postmanu koji je bio connectovan na WebSocket kao drugi klijent dobija response o kreiranom nalogu.
 
5. Primanje top 10 BUY naloga putem WebSocket-a
Endpoint:
ws://localhost:8080/ws/orders pomoću ovog url-a se konektujemo na webSocket
Message:
TOP_BUY 
Response:
[
  {
    "id": 20,
    "amount": 5,
    "price": 135567,
    "type": "BUY"
  }
 
6. Primanje top 10 SELL naloga putem WebSocket-a
Endpoint:
ws://localhost:8080/ws/orders pomoću ovog url-a se konektujemo na webSocket
Message:
TOP_SELL
Response:
[
  {
    "id": 33,
    "amount": 5,
    "price": 25,
    "type": "SELL"
  },
  {
    "id": 36,
    "amount": 5,
    "price": 43,
    "type": "SELL"
  },
  {
    "id": 6,
    "amount": 200,
    "price": 45,
    "type": "SELL"
  },
 



Order Matching Pravila
•	BUY nalog može da se upari samo sa SELL nalogom koji ima istu cenu i količinu.
•	SELL nalog može da se upari samo sa BUY nalogom koji ima istu cenu i količinu.
•	Ako se ne pronađe odgovarajući nalog, novi nalog se čuva u bazi i čeka par.

STRUKTURA KODA:
• Controller – Prima HTTP i WebSocket zahteve i poziva servis.
• Service – Obrađuje poslovnu logiku i komunicira s repozitorijumom.
• Repository – Omogućava pristup bazi podataka koristeći R2DBC.
• WebSocket Handler – Upravlja WebSocket komunikacijom između servera i klijenata.
• WebSocket Configuration – Konfiguriše WebSocket endpoint-ove i registruje handler.
• Entity – Modeli podataka koji se čuvaju u bazi.
• Application – Ulazna tačka aplikacije.
Kako su klase povezane:
• OrderController → poziva OrderService za obradu REST API zahteva.
• OrderService → koristi OrderRepository za interakciju s bazom podataka.
• OrderRepository → komunicira s PostgreSQL bazom koristeći R2DBC.
• OrderWebSocketHandler → obrađuje WebSocket zahteve i takođe poziva OrderService.
• OrderService → šalje obaveštenja WebSocket handleru (OrderWebSocketHandler) kada se kreira novi nalog.
• WebSocketConfiguration → Registruje OrderWebSocketHandler i omogućava WebSocket konekcije.
• Application → pokreće Spring aplikaciju i inicijalizuje sve bean-ove.
                  
Neblokirajući / Asinhroni Dizajn
Aplikacija koristi Reactive Programming kako bi obezbedila neblokirajuće i efikasne operacije:
R2DBC – omogućava neblokirajuću interakciju sa bazom podataka.
Mono/Flux (Reactor) – koristi se za asinhrono procesiranje i obradu podataka.
WebSocket + Reactive Streams – omogućava real-time komunikaciju bez blokiranja thread-ova.

NAPOMENA: U kodu postoje zakomentarisani delovi koji su ostavljeni radi objašnjenja toka misli i zbog nadogradnje same aplikacije naknadno kada vreme ne bude ograničavajući faktor.







