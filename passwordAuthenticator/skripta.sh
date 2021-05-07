#!/bin/bash


# Priložena skripta za demonstraciju rada programa ne funkcionira ukoliko se u alatima koristi System.console() za unos zaporki, ali se koristi takav pristup
# jer je jedna od potencijalnih prijetnji shoulder surfing pa je poželjno sakriti unos.
# Zbog toga, skripta služi samo za orijentaciju kao bi se uspješno ručno demonstrirale značajke alata, uključujući rubne slučajeve
# (iako bi sve normalno funkcioniralo putem skripte ako se u alatima koristi Scanner za unos, no tada bi se izgubilo skrivanje upisanih zaporki).


# promjena trenutnog direktorija
cd src/main/java/hr/fer/srs

# generiranje .class datoteka
javac -d ../../../../../../bin *.java commands/*.java elements/*.java exceptions/*.java

# povratak u pocetni direktorij
cd ../../../../../../

printf "#USER MANAGEMENT#\n"
printf "\n\n#unos neispravnog korisnickog imena - previse znakova#\n"
java -cp bin hr.fer.srs.UserMgmt add YW0xwIFMGZLFQF7CM9uZFLq0ZukCZuVyi94lfPSAtNBlEmjp1GOSM99k7fDKSqjlWUsr8iexoaMIeDI4qob2Hfvzmgibr4hU6XZvg1NSSas2NlBiHANbj8xI2ZfkJS3gMtv3Ulny0fFZpDtREIXPIrFH8tIRhchDyfwPOPCMUm6ONRy7g9jqggG5y0KVTjrLzMFbzyNnNh9FCzCWXwq9LpMbA1sqeqlfxm3k9GNfR7pKH7Lg8WXjh2AtMe3OeNJ3rJJec1Hfa7mBb45QMVE6s0sG

printf "\n\n#unos neispravne zaporke - previse znakova#\n"
java -cp bin hr.fer.srs.UserMgmt add user <<EOF
YW0xwIFMGZLFQF7CM9uZFLq0ZukCZuVyi94lfPSAtNBlEmjp1GOSM99k7fDKSqjlWUsr8iexoaMIeDI4qob2Hfvzmgibr4hU6XZvg1NSSas2NlBiHANbj8xI2ZfkJS3gMtv3Ulny0fFZpDtREIXPIrFH8tIRhchDyfwPOPCMUm6ONRy7g9jqggG5y0KVTjrLzMFbzyNnNh9FCzCWXwq9LpMbA1sqeqlfxm3k9GNfR7pKH7Lg8WXjh2AtMe3OeNJ3rJJec1Hfa7mBb45QMVE6s0sG
EOF

printf "\n\n#unos neispravne zaporke - nedovoljno znakova#\n"
java -cp bin hr.fer.srs.UserMgmt add user <<EOF
um1!
EOF

printf "\n\n#unos neispravne zaporke pri dodavanju novog korisnika - nedostatak broja#\n"
java -cp bin hr.fer.srs.UserMgmt add user <<EOF
userManagement!
EOF

printf "\n\n#unos neispravne zaporke pri dodavanju novog korisnika - nedostatak velikog slova#\n"
java -cp bin hr.fer.srs.UserMgmt add user <<EOF
usermanagement!1
EOF

printf "\n\n#unos neispravne zaporke pri dodavanju novog korisnika - nedostatak specijalnog znaka#\n"
java -cp bin hr.fer.srs.UserMgmt add user <<EOF
userManagement1
EOF

printf "\n\n#unos neispravne ponovljene zaporke#\n"
java -cp bin hr.fer.srs.UserMgmt add user <<EOF
userManagement!1
userManagement!1!
EOF

printf "\n\n#uspjesno dodavanje novog korisnika#\n"
java -cp bin hr.fer.srs.UserMgmt add user <<EOF
userManagement!1
userManagement!1
EOF

printf "\n\n#uspjesno dodavanje novog korisnika#\n"
java -cp bin hr.fer.srs.UserMgmt add korisnik <<EOF
userManagement!1
userManagement!1
EOF

printf "\n\n#uspjesno dodavanje novog korisnika#\n"
java -cp bin hr.fer.srs.UserMgmt add usario <<EOF
userManagement!1
userManagement!1
EOF

printf "\n\n#dodavanje korisnika koji vec postoji#\n"
java -cp bin hr.fer.srs.UserMgmt add user

printf "\n\n#poziv nepodrzane naredbe#\n"
java -cp bin hr.fer.srs.UserMgmt pwd user

printf "\n\n#poziv naredbe bez navodjenja korisnickog imena#\n"
java -cp bin hr.fer.srs.UserMgmt passwd

printf "\n\n#neuspjesan pokusaj promjene zaporke u vec koristenu zaporku za tog korisnika#\n"
java -cp bin hr.fer.srs.UserMgmt passwd user <<EOF
userManagement!1
userManagement!1
EOF

printf "\n\n#neispravan unos ponovljene zaporke#\n"
java -cp bin hr.fer.srs.UserMgmt passwd user <<EOF
UserManagement!1
userManagement!1
EOF

printf "\n\n#uspjesna promjena zaporke#\n"
java -cp bin hr.fer.srs.UserMgmt passwd user <<EOF
UserManagement!1
UserManagement!1
EOF

printf "\n\n#pokusaj promjene zaporke nepostojeceg korisnika#\n"
java -cp bin hr.fer.srs.UserMgmt passwd benutzer

printf "\n\n#brisanje korisnika#\n"
java -cp bin hr.fer.srs.UserMgmt del korisnik

printf "\n\n#brisanje nepostojeceg korisnika#\n"
java -cp bin hr.fer.srs.UserMgmt del benutzer

printf "\n\n#uspjesno forsiranje promjene zaporke korisnika pri iducoj prijavi#\n"
java -cp bin hr.fer.srs.UserMgmt forcepass usario

printf "\n\n#neuspjesno forsiranje promjene zaporke pri iducoj prijavi - nepostojeci korisnik#\n"
java -cp bin hr.fer.srs.UserMgmt forcepass benutzer

printf "\n\n\n#LOGIN#\n"

printf "\n\n#neuspjesna prijava korisnika - nepostojeci korisnik#\n"
java -cp bin hr.fer.srs.Login login korisnik <<EOF
userManagement!1
userManagement!1
userManagement!1
EOF

printf "\n\n#neuspjesna prijava korisnika - neispravna zaporka#\n"
java -cp bin hr.fer.srs.Login login user <<EOF
userManagement!1
userManagement!1
userManagement!1
EOF

printf "\n\n#uspjesna prijava korisnika#\n"
java -cp bin hr.fer.srs.Login login user <<EOF
UserManagement!1
EOF

printf "\n\n#uspjesna prijava korisnika - neuspjeli prvi pokusaj#\n"
java -cp bin hr.fer.srs.Login login user <<EOF
userManagement!1
UserManagement!1
EOF

printf "\n\n#nespjesna prijava korisnika i promjena zaporke - neispravan unos ponovljene zaporke#\n"
java -cp bin hr.fer.srs.Login login usario <<EOF
userManagement!1
UserManagement!1
userManagement!1
EOF

printf "\n\n#uspjesna prijava korisnika i promjena zaporke#\n"
java -cp bin hr.fer.srs.Login login usario <<EOF
userManagement!1
UserManagement!1
UserManagement!1
EOF

printf "\n\n#forsiranje promjene zaporke korisnika pri iducoj prijavi#\n"
java -cp bin hr.fer.srs.UserMgmt forcepass usario

printf "\n\n#uspjesna prijava korisnika, neuspjesna promjena zaporke - promjena zaporke u vec koristenu zaporku za tog korisnika#\n"
java -cp bin hr.fer.srs.Login login usario <<EOF
UserManagement!1
userManagement!1
userManagement!1
EOF

printf "\n\n#promjena podataka unutar datoteke s podacima o korisnicima#\n"
sed -i 's/^./123/g' database.txt

printf "\n\n#provjera nemogucnosti daljnjeg koristenja trenutne inacice alata, unatoc ispravno unesenim podacima#\n"
java -cp bin hr.fer.srs.Login login usario

printf "\n\n#promjena imena datoteke koja predstavlja alat za pohranu zaporki - analogno brisanju datoteke#\n"
printf "#smatra se da ne postoji nikakva inacica alata pa se stvara nova datoteka za nove unose\n"
mv database.txt bazaPodataka.txt
mv databaseHash.txt bazaPodatakaHash.txt
java -cp bin hr.fer.srs.UserMgmt add user <<EOF
userManagement!1
userManagement!1
EOF