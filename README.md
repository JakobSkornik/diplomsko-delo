# **Strojno učenje za kombinatorično optimizacijo na problemu usmerjanja vozil**

## **Poglavje 1**
## **UVOD**

Ko so ljudje želeli matematično definirati naravne probleme, se je izkazalo, da nekateri problemi nimajo učinkovitega načina reševanja. S preučevanjem takšnih problemov in metodologij reševanja se je začel razvoj teorije izračunljivosti v tridesetih letih 20. stoletja. Kot merilo za zahtevnost problema, se je uveljavil model Turingovega stroja. Glede na to koliko časa bi Turingov stroj porabil za rešitev problema, se izoblikujeta dva glavna razreda težavnosti problemov. Problemi, ki jih deterministični Turingov stroj reši v polinomskem času, spadajo v razred P (angl. *deterministic polynomial time*). Problemi, ki jih reši v polinomskem času nedeterministični Turingov stroj, pa spadajo v razred NP (angl. *non-deterministic polynomial time*). Vprašanje, ali sta ta dva razreda enaka, predstavlja še vedno eno večjih nerešenih matematičnih ugank.

Del problemov razreda NP predstavlja kombinatorična optimizacija. To so problemi, kjer iščemo najboljšo rešitev iz diskretne množice rešitev. Zaradi kombinatorične narave problemov je množica možnih rešitev zelo velika, zato izčrpno iskanje ni smiselno za večje probleme. Reševanje takšnih problemov se je usmerilo v iskanje približka rešitve, ki pa ga je možno najti v sprejemljivem času. Takšnim metodam pravimo hevristične metode.

Strojno učenje je hitro razvijajoča se veja umetne inteligence. Algoritmi strojnega učenja si zgradijo matematični model na podlagi vhodnih podatkov, s katerim so zmožni opravljati napovedi na novih, še neznanih podatkih. Procesu grajenja modela pravimo učenje. Strojno učenje se je izkazalo za posebej učinkovito na naravnih podatkih, kjer ni jasno vidnih matematičnih odvisnosti. V zadnjih letih se je še posebej razširilo globoko učenje. Metode globokega učenja temeljijo na umetnih nevronskih mrežah. Kot že samo ime pove, je navdih pridobljen v nevronskih strukturah v možganih. Nevroni so sestavljeni v nevronske plasti, vsaka plast pa je popolnoma povezana s sosednima plastema. Vsaka plast lahko opravlja tudi drugačno nalogo. Zaradi večplastne strukture, imenujemo to obliko učenja globoko učenje. Zaradi popolne povezanosti nevronskih plasti in posledično velike kombinatorične zmogljivosti, ima globoko učenje zmožnost približati zapletene matematične funkcije.

Namen tega dela je uporaba metod strojnega učenja za kombinatorično optimizacijo. Izbran problem kombinatorične optimizacije se imenuje problem usmerjanja vozil. Gre za iskanje optimalne množice poti čez ciljna vozlišča za vozila z omejeno nosilnostjo. Vsaka posamezna pot pa se začne in konča v enem izhodišču. Problem se predstavi z grafom, kjer ima vsako vozlišče težo (lahko enako 0), vsaka povezava svojo razdaljo in označeno ciljno izhodišče. Težko je definirati lastnosti grafa, s katerimi bi lahko našli dobro rešitev. Tu se strojno učenje, še posebej globoko učenje, izkaže za primernega kandidata. Poraja se vprašanje ali je možno zadovoljivo definirati lastnosti grafa z globokim učenjem.

V poglavju (2) je predstavljena kombinatorična optimizacija in problem usmerjanja vozil. V poglavju (3) je predstavljeno strojno učenje in metode, ki so uporabljene v tem delu. Temo sledi poglavje z preizkusi. Rezultati preizkusov so v naslednjem poglavju. V poglavju (6) so sklepne besede in ugotovitve.

___________


## **Poglavje 2**
## **KOMBINATORIČNA OPTIMIZACIJA**

Matematična optimizacija je teorija iskanja in računanja optimumov funkcij. Kadar je zaloga vrednosti diskretna, problem pa je kombinatorične narave, govorimo o kombinatorični optimizaciji. Znana primera takšnih problemov sta **problem 01-nahrbtnika**, angl. *the knapsack problem*, in **problem potujočega trgovca**, angl. *traveling salesman problem*. Večina teh problemov je težko izračunljivih.

### **2.1 Teorija izračunljivosti in računske zahtevnosti**

Ko govorimo o težki izračunljivosti, s tem namigujemo, da problema ne znamo učinkovito in točno rešiti. Težavnost posameznega problema določimo z najhitrejšim znanim algoritmom, ki problem točno reši. Da lahko formalno ocenimo zahtevnost problema, moramo najprej definirati Turingov stroj. Turingov stroj je teoretični model, ki zna izračunati vse, kar se izračunati da. Z njim lahko ovrednotimo zahtevnost in izračunljivost posamezne oblike problemov. Poglejmo si kako Turingov stroj deluje, vrste Turingovih strojev in kam uvrsti izbran problem. 

Turingov stroj je sestavljen iz:
* neskončnega traka, 
* bralno-pisalne glave in 
* nadzorne enote. 

**Trak** je sestavljen iz posameznih celic. V vsaki celici je simbol iz končno velike abecede. Če znaka ni, se v celico zapiše poseben simbol, ki nakazuje prazno celico. 

Branje in pisanje na trak opravlja **bralno-pisalna glava**. V vsakem koraku izvajanja glava prebere simbol, v celico zapiše nov simbol in opravi premik. Premik je lahko v levo, desno ali pa ostane na mestu. 

Vsako akcijo glave diktira **nadzorna enota**. Ta spremlja v katerem stanju se stroj nahaja. Glede na stanje in kateri simbol glava prebere, nadzorna enota izbere akcijo. Izbira akcije je definirana s funkcijo prehodov. Za formalno definicijo potrebujemo torej 7 podatkov. Trak, vhodno besedo, abecedo traku, funkcijo prehodov, začetno stanje, simbol za prazno celico in množico končnih stanj.
Ko pride stroj v eno izmed končnih stanj in nima več možnega prehoda, se izvajanje zaključi.

Turingov stroj M je torej definiran kot:

    M = (Q, Epsilon, Gamma, delta, q_0, B, F), kjer je:
        Q... končna množica stanj,
        Epsilon... vhodna beseda (Eps *subset of* Gamma),
        Gamma... končna množica simbolov abecede traku,
        delta... funkcija prehodov,
        q_0... začetno stanje (q_0 *element of* Q),
        B... simbol za prazno celico (B *element of* Gamma),
        F... množica končnih stanj (F *subset of* Q).

Pred izvajanjem je potrebno na trak napisati vhodno besedo. Turingov stroj je uspešno opravil nalogo, če je po izvajanju ustavljen v končnem stanju. Rečemo, da Turingov stroj sprejme vhodno besedo. Vse besede, ki jih nek Turingov stroj sprejme (lahko tudi neskončno veliko besed) tvorijo jezik Turingovega stroja. 

Formalna definicija jezika za Turingov stroj *M* se glasi:

    L(M) = {w|w element of Epsilon* and M sprejme w}
  
Funkcijo prehodov si lahko predstavljamo kot tabelo. Stolpci predstavljajo stanja, vrstice pa vhodne simbole. Simbol in stanje nam tako opredeljujeta želeno celico tabele. 

|Simbol/Stanje|Q1|Q2|Q3|Qf|
|---|---|---|---|---|
|a|(a,Q2,R)|(a,Q1,L)|(b,Q2,R)|(b,Q1,L)|
|b|(a,Q3,R)|...|...|...|
|c|...|...|...|...|
|B|...|...|...|...|

Primer tabele prehodov.

Glede na funckijo prehodov se pojavita dve obliki Turingovih strojev. **Deterministični Turingov stroj** ima vsako celico zapolnjeno z natanko eno akcijo. **Nedeterministični Turingov stroj** te omejitve nima. To pomeni, da je lahko v celici več akcij za isti par stanja in simbola.
Formalna definicija funkcije prehodov se glasi:

    Q x Gamma -> Q x Gamma x {L, D}; L premik v levo, D premik v desno.
  
Nadzorna enota nedeterminističnega Turingovega stroja vedno izbere optimalno akcijo, kadar jih je na voljo več. V praksi optimalne akcije seveda ne moremo identificirati v prihodnost. Nedeterministični Turingov stroj je torej zgolj teoretično orodje. 

Velja, da je deterministični Turingov stroj po zmogljivosti ekvivalenten nedeterminističnemu. Vse kar zmore nedeterministični zmore tudi deterministični Turingov stroj. Funkcijo prehodov nedeterminističnega Turingovega stroja je moč prevesti na deterministično funkcijo prehodov. Ker ima deterministični Turingov stroj omejitev akcij na celico v tabeli, je tabela prehodov za deterministični Turingov stroj po navadi večja od tabele nedeterminističnega. Posledično je potrebno tudi več korakov za dosego istega rezultata. Izguba v učinkovitosti izvajanja po prevedbi je ključnega pomena pri ocenjevanju zahtevnosti posameznih problemov.

Poglejmo si primer, ki dobro nakaže razliko med determinističnim in nedeterminističnim strojem. Na traku je zapisan neskončno dolg niz ničel, mogoče pa se nekje nahaja ena enica. Naša naloga je napisati funkcije prehodov za obe obliki Turingovega stroja. V nedeterminističnem stroju je rešitev enostavna. Ker se nedeterministični Turingov stroj nedeterministično odloči za pravilno akcijo v celici, se bo sam premaknil v smer proti ničli, če pa ničle ni se pa ustavi.

||Q1|
|---|---|
|0|(0,Q1,L), (0,Q1,R)|
|1|(1,Qf,-)|

Tabela prehodov za NTS.

Pri determinističnemu je nekoliko bolj zapleteno saj nevemo v kateri smeri na traku je enica. Preostane nam nič drugega kot da preiščemo vse možne celice. Preverimo celici, ki sta od začetne celice oddaljene za eno polje. Nato celici, ki sta oddaljeni za dve polji. Postopek se nato ponavlja dokler ne najdemo enice. Prav tako nam deterministični Turingov stroj ne zna odgovoriti na vprašanje, ali je na traku kakšna enica. V primeru, da enice ni, se postopek izvaja v neskončnost, odgovora pa ne dobimo.

Zahtevnost je količina, ki nam podaja razmerje med neko mero in dolžine vhodne besede. Kadar nas zanima časovna zahtevnost, ocenjujemo število korakov, ki bi ga deterministični Turingov stroj porabil za rešitev problema. Kadar nas zanima prostorska zahtevnost, govorimo o številu celic na traku, ki bi jih deterministični Turingov stroj porabil za rešitev problema.

Razmerje med jezikom in problemom dobimo tako, da vsako instanco problema predstavimo z besedo iz jezika L(M) za Turingov stroj M. Razred problemov, ki jih deterministični Turingov stroj reši s polinomsko časovno zahtevnostjo je razred P. Razred problemov, ki jih Turingov stroj nedeterministično reši v polinomskem času pa imenujemo razred NP. Vidimo torej, da je razred P podmnožica razreda NP.

### **2.2 NP-težki problemi**

Razred **NP** vsebuje tudi podmnožico problemov, ki ji pravimo **NP-poln** razred. Če lahko vsak problem iz razreda **NP** prevedemo v polinomskem času v problem *Pi*, je problem *Pi* v razredu **NP-poln**.

**NP-težek** razred, je poleg razreda **NP** posebna množica problemov. Problem *Pi* je **NP-težek**, ko obstaja problem *Pj* v razredu **NP-poln**, ki ga je možno v polinomskem času prevesti v *Pi*. Razred **NP-poln** je torej ravno presek med razredom **NP** in razredom **NP-težek**. Nekateri **NP-težki** problemi niso rešljivi v polinomskem času tudi na nedeterminističnih Turingovih strojih. Problem usmerjanja vozil je **NP-težek** problem. Ker za **NP-težke** probleme ne poznamo učinkovitih, algoritmov, se je kot standarden način reševanja uveljavilo iskanje približnih rešitev. Takemu načinu reševanja rečemo *hevristično reševanje*. 

### **2.3 Hevristično programiranje**

Beseda hevristično izhaja iz stare grške besede *heurios* (*spoznam, ugotovim*). Že ime samo nam pove, da temelji pristop hevrističnih algoritmov na načelih raziskovanja, odkrivanja in prilagajanja. 

Problem potujočega trgovca je klasičen problem *kombinatorične optimizacije*. Podan imamo graf G = < V, E >, kjer je *V* množica vozlišč in *E* množica povezav. Vsaka povezava ima podano tudi svojo dolžino. Zanima nas najkrajši možni obhod, kjer so obiskana vsa vozlišča vsaj enkrat. Poenostavljena oblika problema ima podana samo vozlišča z njihovimi koordinatami G= < V >. V tem primeru iščemo le permutacijo množice *V*, ki ima najmanjšo skupno razdaljo. 

Najpreprostejši način za rešitev tega problema je, da preprosto naštejemo vse permutacije množice *V* in jim izračunamo kakovost. Na koncu izberemo *najkvalitetnejšo permutacijo*. Če je velikost množice enako *|V| = n*, je število vseh možnih permutacij enako *n!*. Takšnega števila permutacij ni smiselno računati tudi že za zelo majhne *n* (15! = 1307674368000).

Mero, ki jo želimo optimizirati, imenujemo **kakovost rešitve**. Kakovost rešitve je lahko katerakoli mera, ki jo želimo optimizirati. Lahko optimiziramo čas potreben za potovanje, finančni strošek potovanja ali pa porabo goriva. Izbrana mera za kakovost rešitve in vhodni podatki nam definirajo *prostor rešitev*.

**Prostor rešitev** je abstraktna definicija vseh možnih rešitev v kombinaciji z njihovo kakovostjo. Pri minimizaciji je globalni minimum prostora točka, ki predstavlja optimalno rešitev. Hevristike pogosto temeljijo na načelu preiskovanja prostora rešitev. Spremljajo spremembe v rešitvah in usmerjajo iskanje proti predvidenimi boljšimi rešitvami.

Hevristike posledično ne zagotavljajo, da je končna rešitev optimalna. Tudi če je končna rešitev v optimumu, hevristika ne zagotavlja, da je ta globalen. Neglobalni optimumi predstavljajo velik problem hevristikam, zato so se razvile mnoge metode, ki se jim poskušajo izogniti.

Ena izmed najbolj znanih metahevristik je **simulirano ohlajanje**, angl. *simulated annealing*. Je ena izmed mnogih naravno inspiriranih hevristik. Navdih je pridobljen iz simuliranega ohlajanja kovin. Uporabljena je formula za notranjo energijo fizičnega sistema:

    P(S) = e ^ (-(S - Sbest) / T)

V algoritmu je formula uporabljena kot verjetnost, da se izbere slabša rešitev v posamezni iteraciji. Temperatura se postopoma manjša, z njo pa se manjša tudi verjetnost za prehod v slabšo rešitev. Na začetku je izbor rešitev skoraj popolnoma naključen z namenom, da se preišče čim širši obseg rešitev. Iskanje se nato počasi usmeri v najboljši optimum.

Premike med rešitvami ustvarja na podlagi ideje o soseščini. Vsaka rešitev ima definirano svojo soseščino. Funkcijo, ki nam vrne soseščino definiramo:

    N(Si) = {S1, S2, ..., Sn}.

V vsaki iteraciji, je izbrana naključna rešitev iz soseščine. Funkcija, ki nam vrača soseščino, lahko ustvarja soseščine na več načinov. Izberemo lahko načine, ki bolj ustrezajo zadani nalogi. V primeru iskanja optimalne permutacije je lahko soseščina definirana, kot vse možni premiki posameznih elementov. **Lokalno iskanje** je skupno ime za metode, kjer se preiskuje prostor rešitev z ustvarjanjem soseščin.

Nekateri drugi kombinatorični problemi imajo v definiciji podane še omejitve. Na primer, v problemu usmerjanja vozil s časovno omejitvijo, se mora vsaka posamezna pot zaključiti v določenem času. Take omejitve vplivajo na prostor možnih rešitev. Razdelijo ga na del, kjer so rešitve sprejemljive in kjer niso. Omejitve je potrebno preučiti in jih primerno upoštevati. Lahko jih upoštevamo pri ustvarjanju soseščin in vedno ustvarimo le množico sprejemljivih rešitev. Druga možnost je, da nesprejemljive rešitve posebej obravnamo pri izračunu kvalitete in ji dodamo kazen. S tem želimo iskanje usmeriti proti sprejemljivim rešitvam.

### **2.4 Problem usmerjanja vozil**

Problem usmerjanja vozil zajema družino problemov, kjer je na voljo zaloga vozil, potrebno pa je izvršiti zalogo zahtev. Vpeljemo lahko še dodatne omejitve in s tem definiramo obliko problema usmerjanja vozil (od sedaj naprej VRP). Neštejmo nekaj oblik problema:

* VRP z omejeno nosilnostjo (angl. *Capacitated VRP*) ali **CVRP**
* VRP s časovnimi okni (angl. *VRP with Time Windows*) ali **VRPTW**
* VRP z več izhodišči (angl. *Multi Depot VRP*) ali **MDVRP**

Izbran problem je problem usmerjanja vozil z omejeno nosilnostjo. Da lahko problem matematično definiramo, podamo sedaj definicije potrebnih parametrov.

    - N = {1,... ,n}...     množica strank
    - d = {d1,... ,dn}...   množica naročil
    - {0}...                množica izhodišč
    - L = {1,...}...        množica vozil
    - C...                  konstanta, ki predstavlja nosilnost posameznega vozila

S pomočjo teh parametrov lahko natančneje definiramo izbran problem. Količina *di* neke dobrine mora biti dostavljena (ali odvožena) za vsako stranko *i*(iz množice N) iz začetnega vozlišča 0. Uporabljeno je lahko poljubno število enakih vozil s kapaciteto C. Minimizira se skupna cena posameznih obvozov. Podajmo omejitve:

    (1) vsak obvoz se začne in konča na izhodišču,
    (2) vsaka stranka je obiskana enkrat,
    (3) zahteva posamezne stranke ne presega nosilnosti vozila.

Da lahko definiramo skupno ceno oziroma kvaliteto rešitve, podamo še spremenljivko:

    x_rij... vozilo r (r={1,...,p}) prevozi povezavo med i in j
    
Ceno rešitve lahko potem označimo kot:

    (1) min ( sum_r sum_i sum_j c_ij * x_rij )

z omejitvami:

    (2) BORCINOVA 7_4528
    (3)
    (4)
    (5)
    (6)
    (7)

Enačba (1) nakazuje iskanje minimalne cene. Omejitev (2) zagotovi, da je vsaka stranka obiskana natanko enkrat. Omejitvi (3) in (4) zagotovita, da se vsaka pot začne in konča v izhodišču. Rečemo jima tudi omejitvi pretoka. Omejitev (5) poda omejitev nosilnosti vozila. Omejitve (6) zagotovijo, da v rešitvi ni ciklov, ki so odrezani od izhodišča. Zadnja omejitev (7) samo poda definicijska območja spremenljivk. Taki obliki zapisa pravimo *zapis s tremi indeksi*.

Rešitev problema je torej množica poti. Vsaka posamezna pot predstavlja pot posameznega vozila. Če postavimo nosilnost posameznega vozila na neskončno, dobimo klasičen primer potujočega trgovca. Problem usmerjanja vozil je torej posplošitev problema potujočega trgovca. Omejitev nosilnosti je potrebno upoštevati pri definiciji soseščine ali pa pri računanju kvalitete posamezne rešitve.

Kadar delamo z algoritmi lokalnega iskanja, potrebujemo tudi začetno rešitev. Najenostavnejši način za pridobitev začetne rešitve je uporaba preproste hevristike. Posebej primerne za to so požrešne ali pa naključne metode. Požrešne metode na vsakem odločitvenem koraku izberejo najboljšo možnost. Posledično so zelo hitre, vendar se ujamejo v prvi maksimum na katerega naletijo. Naključne metode na vsakem odločitvenem koraku izberejo naklkučno možnost. Včasih so naključne metode tudi bolj uporabne kot požrešne, saj je v začetnih korakih lokalnega iskanja naključnost zaželena.

Požrešna metoda za problem usmerjanja vozil v vsakem koraku izbere najbližjo stranko, ki še ne prekorači nosilnosti. Ko take stranke ni več, se vrne v izhodišče. Nato se postopek ponavlja dokler niso vse stranke obiskane. Poti med strankami lahko rekonstruiramo s Floyd-Warshallovim algoritmom. Algoritem nam vrne seznam najkrajših možnih poti med vsemi vozlišči grafa.

___________


## **STROJNO UČENJE**

Začetki umetne inteligence segajo v štiridesta leta dvajsetega stoletja, dramatično naznanjeni z vprašanjem Alana Turinga, 'Lahko stroji razmišljajo?'. Alan Turing je zasnoval tudi idejo o Turingovem stroju. Kljub temo, da je bila tehnologija deležna velikih napredkov, je fundamentalna razlika v inteligenci med ljudmi in stroji ostala enaka. Strojno učenje je področje umetne inteligence, ki temelji na učenju sistemov. Temelji na procesiranju podatkov in ustvarjanju funkcije, ki nato preslika vhodne podatke čim natančneje v izhodne. Učenje je torej postopek ustvarjanja približka neznane funkcije. Strojno učenje nam omogoča reševanje nalog, kjer je izrecno programiranje zamudno in nesmiselno. 

### **3.1 Osnove strojnega učenja**

Človek ima pet čutov, od katerih nenehno prejema informacije. Človeški možgani najprej informacije uredijo, nato jih preučijo in sprejmejo odločitev in usmeri ciljni del telesa. Cikel se nato ponovi, rezultat prejšne akcije pa se lahko shrani v spomin. Na podoben način delujejo algoritmi strojnega učenja. Statistično obdelovanje vhodnih podatkov predstavlja temelje strojnega učenja. Po končanem učenju, so odločitve sistema posledica vzorcev in lastnosti vhodnih podatkov. Vprašamo se lahko, kako so v resnici pametni takšni sistemi.

Strojno učenje lahko ločimo na tri glavne skupine:
* nadzorovano učenje,
* nenadzorovano učenje,
* spodbujevano učenje.

Med sabo se razlikujejo zaradi različne oblike vhodnih podatkov, ki jih sprejmejo. Nadzorovano učenje se uporablja z označenimi vhodnimi podatki, nenadzorovano se uporablja z neoznačenimi, spodbujevano pa podatke pridobiva na podlagi akcij, ki jih opravlja v nekem okolju.

Cilj **nadzorovanega učenja** je ustvarjanje približka funkciji na podlagi označenih vhodnih podatkov. Vsak vhodni podatek ima označeno ciljno vrednost. Ciljna vrednost daje nadzorovanemu učenju razlago trenutnega podatka. Skupaj tvorita učni primer. Z učenjem si nadzorovan sistem poskuša ustvariti funkcijo, ki vsak vhodni podatek preslika čim bližje ciljni vrednosti. Glede na obliko ciljne vrednosti ločime dve večji skupini nadzorovanega učenja. Kadar je zaloga ciljne vrednosti diskretna množica, govorimo o klasifikacijskih algoritmih. Kadar pa je zaloga ciljne vrednosti interval, pa govorimo o regresijskih algoritmih.

**Nenadzorovano učenje** se uporablja na problemih, kjer nimamo ciljnih vrednosti v učnih primerih. Primerno je za probleme, kjer nas zanimajo notranje odvisnosti in skrite lastnosti podatkov. Pogost problem, kjer je primerno nenadzorovano učenje, je gručenje neoznačnih podatkov. 

**Spodbujevano učenje** temelji na opazovanju interakcij izbrane akcije z okoljem. Inteligenten program, imenovan agent, najprej ovrednoti začetno stanje. Odločitvena funkcija izbere akcijo, agent pa prejme od okolja signal. Signal agentu v obliki nagrade ali kazni ovrednoti izbrano akcijo. Agent si nato zapomni stanje, akcijo in signal. Na podlagi spomina se prilagodi odločitvena funkcija.



