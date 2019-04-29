# PeerToPeerCoin


The repository contains implementation of Peer-to-peer network. The idea of how the network works can be found below (have a look at idea.png):


PeeToCoin Consensus algorithm:

ZMIENNE:
BLOCKCHAIN;
REJESTR_HASH_RATE_PEERSOW;
TABLICA_HASH_RATÓW_POPRZEDNIEGO_BLOKU;
TABLICA_ZBANOWANYCH;
STAŁE:
N = 256;
T = 60*10; //600s <=>10 min 
M = 25; //Liczba coinow nagrody dla sieci za wykopanie bloku
MAX_ILE_BLOKÓW_DO_UDOWODNIENIA=15;

WZORY:
WZÓR_1(PEER): 
	1.HASH_RATE_SIECI=0;
	2.Dla każdego Peera (new ITEROWALNY_PEER)z REJESTR_HASH_RATE_PEERSÓW:
		1. HASH_RATE_SIECI=HASH_RATE_SIECI+REJESTR_HASH_RATE_PEERSÓW[ITEROWALNY_PEER]
	3. HASH_RATE_PEERA = REJESTR_HASH_RATE_PEERSÓW[PEER]	
	4. Zwróc HASH_RATE_PEERA/HASH_RATE_SIECI*M;

WZÓR_2(HASH_RATE):
	1. new ILE_HASHY = HASH_RATE*T
	2. new WYNIK = CELL(log_2{ILE_HASHY})
	3. Zwróć WYNIK

WZÓR_3(HASH_RATE):
	1.Znajdź najmniejsze, naturalne  S (new S) takie, że 2^S>HASH_RATE
	2. new X = 1; new MIN_X; new MIN_S;  new MIN_EPS=9999999999999;
	3. Tak długo jak S>=0 wykonuj:
		1.S=S-1
		2. Tak długo jak  X<=MAX_ILE_BLOKÓW_DO_UDOWODNIENIA i X nie jest potęgą 2 wykonuj:
			1. Jeżeli ABS(X*2^S-H)>MIN_EPS to:
				1.MIN_EPS = ABS(X*2^S-H)
				2.MIN_S = S
				3.MIN_X = X
			2.X=X+1	
		3. X=X+1;
	4. Zwróć MIN_S, MIN_X;

WZÓR_4(PEER): 
	1.HASH_RATE_SIECI=0;
	2.Dla każdego Peera (new ITEROWALNY_PEER)z REJESTR_HASH_RATE_PEERSÓW:
		1. HASH_RATE_SIECI=HASH_RATE_SIECI+REJESTR_HASH_RATE_PEERSÓW[ITEROWALNY_PEER]
	3. HASH_RATE_PEERA = REJESTR_HASH_RATE_PEERSÓW[PEER]	
	4. Zwróc HASH_RATE_PEERA/HASH_RATE_SIECI*M

HASH(NONCE):
	1.Wylicz hash przy użyciu SHA-256 z bloku, w którym jako dane podany jest jedynie MERKLE_ROOT

1) Kopanie bloku
##############################################################################################################################################################################################################################################

ALGORYTM:
	1. Ustaw kontener na dane (new DANE), które mogą być typu: transakcje (new TRANSAKCJE), danych o wejściach (new WEJŚCIA), danych o wyjściach (new WYJŚCIA)
	2. Odbieraj dane, które zapisuj do DANE
	3. Jeżeli liczba odebranych danych przekroczy N to:
		1. Sprawdź poprawność wszystkich danych. Dla każdego nowego elementu zbioru DANE (new DANE[]):
			1. Zweryfikuj, czy dane zostały w rzeczywistości podpisane przez osobę, która podaje się jako nadawca - sprawdź podpis cyfrowy.
			2. Sprawdź, czy timestamp danych zgadza się z hashem, który jest w Twoim blockchainie o czasie podanym w timestamp. 
			3. Jeżeli coś jest nie tak z punktami 1 i 2 to określ DANE[] jako błędne, usuń DANE[] z DANE i wróć do ^^.2
			3. Jeżeli(DANE[] jest typu TRANSAKCJE):
				1. Dla każdego id transakcji podanej jako w DANE[] jako transakcja wejściowa (new ID_TRANSAKCJI_WEJŚCIOWEJ) wykonaj:
					1.Wyszukaj ID_TRANSAKCJI_WEJŚCIOWEJ w BLOCKCHAIN i sprawdź, czy nie została ona już wykorzystana (czy nie ma jej już podanej jako transakcji wejściowej)
					2.Jeżeli transakcja identyfikowana przez ID_TRANSAKCJI_WEJŚCIOWEJ była już wykorzystywana jako transakcja wyjściowa to określ DANE[] jako błędne, usuń DANE[] z DANE i wróć do ^^^^.2
			4. Jeżeli(DANE[] jest typu WEJŚCIA):
				1. Spróbuj połączyć się z IP peersa podanym w WEJŚCIA
				2. Jeżeli nie będzie możliwe uzyskanie połączenia:
					1. Określ DANE[] jako błędne, usuń DANE[] z DANE;
					2. Wróć do ^^^^.2
				3. Dodaj Peersa z DANE[] do REJESTR_HASH_RATE_PEERSÓW (jego hash rate pozostaw pusty)
			5. Jeżeli(DANE[] jest typu WYJŚCIA):
				1. Usuń Peersa z DANE[] z REJESTR_HASH_RATE_PEERSÓW
	3. Zbuduj szkielet nowego bloku (new BLOK)
		1. Dodaj hash poprzedniego bloku
		2. Dodaj timestamp bloku
		3. Dla każdego elementu zbioru DANE(new DANE[]):
			1. Dodaj DANE[] do BLOK
		4. Dodaj do bloku informacje o nagrodach za działanie w sieci:
			1. Dla każdego peera (new PEER), którego hash rate użyty w przy kopaniu poprzedniego bloku jest podany w TABLICA_HASH_RATÓW_POPRZEDNIEGO_BLOKU:
				1. Do danych w BLOK dodaj transakcję, która wynagrodzi tego peera, zgodnie ze wzorem ----WZOR_1(PEER)----
			2. Dodaj do bloku transakcję opiewającą na M coinów, z sobą jako adresatem (nagroda za ewentualne wykopanie bloku)
		5. Określ trudność bloku:
			1. Pobierz hash rate sieci (new HASH_RATE) z poprzedniego bloku w BLOCKCHAIN.
			2. Dla każdego wyjścia (new WYJŚCIA[]) w WYJŚCIA wykonaj:
				1. Poszukaj peersa, który wyszedł w REJESTR_HASH_RATE_PEERSÓW
				2. Jeżeli znaleziono tego peersa to:
					1. HASH_RATE = HASH_RATE - REJESTR_HASH_RATE_PEERSÓW[WYJŚCIA[]]
				3. W przeciwnym wypadku:
					1. Zapytaj sąsiednich peersów o HASH_RATE peersa, który wyszedł.
			3. Dla każdego wejścia (new WEJŚCIA[]) w WEJŚCIA wykonaj:
				1. Poszukaj peersa, który wyszedł w REJESTR_HASH_RATE_PEERSÓW
				2. Jeżeli znaleziono tego peersa to:
					1. HASH_RATE = HASH_RATE + REJESTR_HASH_RATE_PEERSÓW[WYJŚCIA[]]
				3. W przeciwnym wypadku:
					1. Zapytaj sąsiednich peersów o HASH_RATE peersa, który wyszedł.
			4. Oblicz trudność bloku, znając HASH_RATE sieci tak, aby czas znalezienia nonce zerującego hash bloku przy danej trudności wynosił T, zgodnie ze ---WZÓR_2(HASH_RATE)---
		6. Ułóż dane w BLOK w merkle tree (new MERKLE_TREE) i ustaw hash korzenia jako merkle root (new MERKLE_ROOT)
	5. Rozpocznij kopanie bloku:
		1. Ustaw zmienną nonce (new NONCE) na zero.
		2. Przygotuj się do procesu udowadniania swojego hash rate:
			1. Jeżeli nie znasz swojego hash rate lub nie wiesz jak go udowodnić to:
				1. Wykonaj algorytm sprawdzający ile hashy na sekunde jesteś w stanie wykonać i zapisz ten wynik jako (new MÓJ_HASH_RATE)
				2. Ustal, ile nonce zerujących pierwsze cyfry bloków o niższej trudności (czyli takich, których hash rate zaczyna się określoną liczbą zer, mniejszą niż liczba zer potrzebna do globalnego wykopania bloku)
				potrzeba, aby wykopać blok. Skorzystaj ze new(ILE_ZER, ILE_BLOKÓW) = ---WZÓR_3(MÓJ_HASH_RATE)---. Dzięki temu dowiesz się, ile wynosi ILE_BLOKÓW - czyli ile nonce, które powodują, że hash zaczyna się liczbą ILE_ZER zer musisz
				znaleźć, aby udowodnić, że Twój hash rate wynosi HASH_RATE.
			3. Ustaw tablicę noncy (new NONCE_ARRAY), które umożliwią Ci udowodnienie swojego HASH_RATE.
		3. Inkrementuj zmienną NONCE
			1. Jeżeli hash bloku obliczony zgodnie z wzorem ---H(NONCE)--- będzie zaczynał się liczbą zer równą ILE_ZER to dodaj ten nonce do NONCE_ARRAY
			2. Jeżeli hash bloku obliczony zgodnie z wzorem ---H(NONCE)--- będzie zaczynał się liczbą zer umożliwiającą wykopanie bloku, to przerwij inkrementację i przejdź do ^.6.
	6. Wyślij wykopany blok pozostałym peersom w sieci. 
		

2) Po odebraniu wykopanego przez kogoś innego bloku:
###################################################################################################################################################################################################################################################

ALGORYTM:	
	1. Przerwij pracę nad kopaniem swojego bloku.
	2. Rozpocznij weryfikację przesłanego bloku
		1. Jeżeli nie masz określonej trudności bloku, to:
			1. Pobierz hash rate sieci (new HASH_RATE) z poprzedniego bloku w BLOCKCHAIN.
			2. Dla każdego wyjścia (new WYJŚCIA[]) w WYJŚCIA wykonaj:
				1. Poszukaj peersa, który wyszedł w REJESTR_HASH_RATE_PEERSÓW
				2. Jeżeli znaleziono tego peersa to:
					1. HASH_RATE = HASH_RATE - REJESTR_HASH_RATE_PEERSÓW[WYJŚCIA[]]
				3. W przeciwnym wypadku:
					1. Zapytaj sąsiednich peersów o HASH_RATE peersa, który wyszedł.
			3. Dla każdego wejścia (new WEJŚCIA[]) w WEJŚCIA wykonaj:
				1. Poszukaj peersa, który wyszedł w REJESTR_HASH_RATE_PEERSÓW
				2. Jeżeli znaleziono tego peersa to:
					1. HASH_RATE = HASH_RATE + REJESTR_HASH_RATE_PEERSÓW[WYJŚCIA[]]
				3. W przeciwnym wypadku:
					1. Zapytaj sąsiednich peersów o HASH_RATE peersa, który wyszedł.
			4. Oblicz trudność bloku, znając HASH_RATE sieci tak, aby czas znalezienia nonce zerującego hash bloku przy danej trudności wynosił T, zgodnie ze ---WZÓR_2(HASH_RATE)---
		2. Sprawdź, czy zadeklarowana w bloku trudność odpowiada trudności, którą masz wyliczoną. 
			1. Jeżeli tak nie jest to odrzuć blok.
		3. Sprawdź czy nonce w bloku zeruje liczbę zer w hashu bloku określoną przez trudność.
			1. Jeżeli tak nie jest to odrzuć blok. 
		4. Sprawdź poprawność hsha poprzedniego bloku.
			1. Jeżeli hash poprzedniego bloku nie zgadza się z hashem bloku, który w Twoim chainie jest ostatni to odrzuć blok.
		5. Sprawdź poprawność danych.
			1.  Dla każdego nowego elementu zbioru DANE (new DANE[]):
				1. Zweryfikuj, czy dane zostały w rzeczywistości podpisane przez osobę, która podaje się jako nadawca - sprawdź podpis cyfrowy.
				2. Sprawdź, czy timestamp danych zgadza się z hashem, który jest w Twoim blockchainie o czasie podanym w timestamp. 
				3. Jeżeli coś jest nie tak z punktami 1 i 2 to określ blok jako błędny.
				4. Jeżeli(DANE[] jest typu TRANSAKCJE):
					1. Dla każdego id transakcji podanej jako w DANE[] jako transakcja wejściowa (new ID_TRANSAKCJI_WEJŚCIOWEJ) wykonaj:
						1.Wyszukaj ID_TRANSAKCJI_WEJŚCIOWEJ w BLOCKCHAIN i sprawdź, czy nie została ona już wykorzystana (czy nie ma jej już podanej jako transakcji wejściowej)
						2.Jeżeli transakcja identyfikowana przez ID_TRANSAKCJI_WEJŚCIOWEJ była już wykorzystywana jako transakcja wyjściowa to określ blok jako nieprawidłowy.
					2. Jeżeli DANE[] opisuje transakcję związaną z faktem wygenerowania nowego bloku:
						1. Jeżeli jest to transakcja przypisująca określonej osobie liczbę coinów za wykopanie bloku to sprawdź, czy nagroda wynosi M coinów. Jeżeli nie to odrzuć blok.
						2. Jeżeli jest to transakcja przypisująca peerowi (new PEER) liczbę coinów proporcjonalnie do włożonej mocy obliczeniowej za kopanie poprzedniego bloku to:
							1.  Sprawdź czy nagroda zgadza się z Tą wyliczoną zgodnie z wzorem ---WZÓR_4(PEER)---. Jeżeli nie to odrzuć blok
		6. Jeżeli wszystko z blokiem jest w porządku to sprawdź zaakceptuj blok poprzez dodanie go do swojego blockchainu.
		7. Poczekaj jak inni peersi zaakceptują blok i rozpoczną wysyłanie dowodów swojego hash rate. (new LISTA_DOWODÓW)
		8. Wyślij stworzony przez siebie blok (ten, w którym Ty widniejesz jako osoba, której przypadła nagroda za wykopanie bloku), częściowo wykopany, jako dowód swojego hash_rate.
		9. Rozpocznij weryfikację hash rate peerów w sieci, którzy brali udział przy kopaniu tego bloku.
			1. Wylosuj ILE_PRÓB osób z LISTA_DOWODÓW. 
			2. Dla każdej wylosowanej osoby (new LISTA_DOWODÓW[]) wykonaj:
				1. Sprawdź podpis cyfrowy celem stwierdzenia, czy osoba rzeczywiście wysłała ten dowód. Jeżeli nie to 
				2. Sprawdź, czy osoba ta widnieje w jako beneficjent nagrody za wykopanie bloku.
				3. Sprawdź timestamp dowodu i hash poprzedniego bloku. 
				4. Sprawdź, na podstawie przesłanych danych, ile bloków i jakiej trudności (new LICZBA_ZER) osoba zadeklarowała się wykopać celem udowodnienia tego, że ma dany hash rate.
				5. new WERYFIKOWANY_HASH_RATE=0
				6. Dla każdego nonce, które ta osoba przesłała (new NONCE) wykonaj:
					1. Wylicz HASH(NONCE)
					2. Jeżeli hash wynikowy zaczyna się określoną liczbą zer to WERYFIKOWANY_HASH_RATE=WERYFIKOWANY_HASH_RATE+2^(LICZBA_ZER)
				7. Sprawdź w REJESTR_HASH_RATE_PEERSÓW jaki hash rate przypisany jest danej osobie i porównaj go z WERYFIKOWANY_HASH_RATE.
				8. Jeżeli wynik z REJESTRU_HASH_RATE_PEERSÓW różni się od wyniku z WERYFIKOWANY_HASH_RATE to:
					1. Wyślij informację do sieci na temat tego, że zmienił się hash rate danego peera i obecnie wynosi on WERYFIKOWANY_HASH_RATE. 
					2. Zmień wartość hash rate peera w REJESTR_HASH_RATE_PEERÓW
			3. Odbieraj wszystkie informacje na temat ewentualnych zmian hash ratów w sieci.
			4. Dla każdej odebranej informacji wykonaj:
				1.Jeżeli peer, który wysłał wiadomość jest w TABLICA_ZBANOWANYCH to zingoruj wiadomość i przejdź do ^.4
				2.Sprawdź podpis cyfrowy celem stwierdzenia, czy osoba rzeczywiście wysłała ten dowód. Jeżeli nie to 
				3. Sprawdź, czy osoba ta widnieje w jako beneficjent nagrody za wykopanie bloku.
				4. Sprawdź timestamp dowodu i hash poprzedniego bloku. 
				5. Sprawdź, na podstawie przesłanych danych, ile bloków i jakiej trudności (new LICZBA_ZER) osoba zadeklarowała się wykopać celem udowodnienia tego, że ma dany hash rate.
				6. new WERYFIKOWANY_HASH_RATE=0
				7. Dla każdego nonce, które ta osoba przesłała (new NONCE) wykonaj:
					1. Wylicz HASH(NONCE)
					2. Jeżeli hash wynikowy zaczyna się określoną liczbą zer to WERYFIKOWANY_HASH_RATE=WERYFIKOWANY_HASH_RATE+2^(LICZBA_ZER)
				8. Sprawdź w REJESTR_HASH_RATE_PEERSÓW jaki hash rate przypisany jest danej osobie i porównaj go z WERYFIKOWANY_HASH_RATE.
				9. Jeżeli wynik z REJESTR_HASH_RATE_PEERSÓW różni się od wyniku z WERYFIKOWANY_HASH_RATE to:
					1. Zmień wartość hash rate peera w REJESTR_HASH_RATE_PEERSÓW
				10. W przeciwnym wypadku, jeżeli wynik się zgadza to dodaj peera, który przesłał tę błędną informację do TABLICA_ZBANOWANYYCH.  
		10. TABLICA_HASH_RATÓW_POPRZEDNIEGO_BLOKU=REJESTR_HASH_RATE_PEERSÓW


