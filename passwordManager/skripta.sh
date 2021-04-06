#!/bin/bash

# promjena trenutnog direktorija
cd src/main/java/hr/fer/srs

# generiranje .class datoteka
javac -d ../../../../../../bin PasswordManager.java commands/*.java elements/*.java exceptions/*.java

#povratak u pocetni direktorij
cd ../../../../../../

printf "#inicijalizacija alata za pohranu zaporki#\n"
java -cp bin hr.fer.srs.PasswordManager init

printf "\n\n#pohrana zaporke 123456789 za adresu www.fer.hr#\n"
java -cp bin hr.fer.srs.PasswordManager put MasterPass www.fer.hr 123456789

printf "\n\n#pohrana zaporke asdfghjkl za adresu www.google.com#\n"
java -cp bin hr.fer.srs.PasswordManager put MasterPass www.google.com asdfghjkl

printf "\n\n#dohvacanje zaporke za adresu www.fer.hr#\n"
java -cp bin hr.fer.srs.PasswordManager get MasterPass www.fer.hr 

printf "\n\n#promjena zaporke za adresu www.fer.hr u 9876#\n"
java -cp bin hr.fer.srs.PasswordManager put MasterPass www.fer.hr 9876

printf "\n\n#dohvacanje zaporke za adresu www.fer.hr#\n"
java -cp bin hr.fer.srs.PasswordManager get MasterPass www.fer.hr 

printf "\n\n#dohvacanje zaporke za adresu www.google.com#\n"
java -cp bin hr.fer.srs.PasswordManager get MasterPass www.google.com

printf "\n\n#dohvacanje zaporke za nepohranjenu adresu#\n"
java -cp bin hr.fer.srs.PasswordManager get MasterPass www.google.hr

printf "\n\n#provjera neispravno unesenih argumenata - \"get www.google.com\"#\n"
java -cp bin hr.fer.srs.PasswordManager get www.google.com

printf "\n#neispravan unos glavne zaporke#\n"
java -cp bin hr.fer.srs.PasswordManager get masterPass www.google.com

printf "\n#dohvacanje zaporke za adresu www.google.com pomocu ispravne glavne zaporke nakon neispravnog unosa#\n"
java -cp bin hr.fer.srs.PasswordManager get MasterPass www.google.com

printf "\n\n#promjena podataka unutar datoteke#\n"
sed -i 's/^./123/g' passwordManager.txt

printf "\n\n#provjera nemogucnosti daljnjeg koristenja trenutne inacice alata, unatoc ispravno unesenim podacima#\n"
java -cp bin hr.fer.srs.PasswordManager put MasterPass www.google.hr zxcvbnm

printf "\n\n#stvaranje nove inacice alata- \"overwrite\" stare, ali neupotrebljive inacice#\n"
printf "#potrebna intervencija korisnika upisom y(es) ili pokretanjem skripte putem (echo \"yes\" | bash skripta.sh)#\n"
java -cp bin hr.fer.srs.PasswordManager init

printf "\n\n#promjena imena datoteke koja predstavlja alat za pohranu zaporki#\n"
printf "#smatra se da ne postoji nikakva inacica alata pa je potrebno stvoriti novu ukoliko zelimo izvrsavati odredjene\n"
mv passwordManager.txt alatZaPohranuZaporki.txt
java -cp bin hr.fer.srs.PasswordManager get MasterPass www.google.hr
