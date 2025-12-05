package com.example.application1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Date
import android.net.Uri
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private val TAG = "LyfecycleTest"
    private var isSupersize = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge() // permette all'app di occupare lo spazio pure sotto alla barra di tato in alto e alla navigazione in basso
        setContentView(R.layout.activity_main) // collega il codice kotlin alla sua interfaccia grafica, il file xml
        //Esercizio 3:
        // Colleghiamo la TextView del layout al codice
        val txtNumber = findViewById<TextView>(R.id.txtPhoneNumber)

        // Controlliamo: chi ci ha aperto? È stata un'azione di tipo DIAL (chiamata)?
        if (intent?.action == Intent.ACTION_DIAL) {

            // intent.data contiene l'URI, tipo "tel:123456789"
            val data = intent.data

            // Estraiamo solo la parte specifica (il numero)
            val phoneNumber = data?.schemeSpecificPart

            // Mostriamo il numero a video
            txtNumber?.text = "Numero da chiamare: $phoneNumber"
        }


        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)
        val mainLayout = findViewById<LinearLayout>(R.id.main)

        // Cerca questa riga e cambiala così:
        val scroll = findViewById<View>(R.id.scroll)
        if (scroll != null) {
            ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, 0)
                insets
            }
        }
        val dateButton = findViewById<Button>(R.id.dateButton)

        dateButton?.setOnClickListener {
            changeDate()
        }
        val numberField = findViewById<EditText>(R.id.numberInput)
        numberField?.setText("12")// appena si avvia l'app il campo sarà gia compilato con quel testo

        val termsCheckBox = findViewById<CheckBox>(R.id.check)
        val continueButton = findViewById<Button>(R.id.continue_button)
        termsCheckBox?.setOnCheckedChangeListener { buttonView, isChecked -> continueButton?.isEnabled = isChecked }

        themeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                mainLayout?.setBackgroundColor(Color.parseColor("#073042"))
            }else{
                mainLayout?.setBackgroundColor(ContextCompat.getColor(this, R.color.AndroidGreen))
            }
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radioG)
        radioGroup?.setOnCheckedChangeListener { _,  checkedId ->
            mostraSceltaSelezionata(checkedId)
        }

        val linkWebButton = findViewById<Button>(R.id.linkWeb)
        linkWebButton?.setOnClickListener { goWeb() }

        // ========================================================================
        // LOGICA PER L'ESERCIZIO "FORM DI REGISTRAZIONE"
        // ========================================================================

        // 1. Trova tutti gli EditText e il Button dal layout
        val nameInput = findViewById<EditText>(R.id.name_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirm_password_input)
        val ageInput = findViewById<EditText>(R.id.age_input)
        val phoneInput = findViewById<EditText>(R.id.phone_input)
        val registerButton = findViewById<Button>(R.id.register_button)

        // 2. Crea una lista di tutti i campi di testo per controllarli facilmente
        val fields = listOf(nameInput, emailInput, passwordInput, confirmPasswordInput, ageInput, phoneInput)

        // 3. Crea un "TextWatcher" riutilizzabile
        // Un TextWatcher è un "ascoltatore" che si attiva ogni volta che il testo in un EditText cambia.
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Non ci serve implementare questo
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Questo si attiva mentre il testo sta cambiando
                // Controlliamo se tutti i campi sono pieni
                val allFieldsFilled = fields.all { it?.text?.isNotEmpty() == true}

                // E se le password corrispondono
                val passwordsMatch = passwordInput?.text.toString() == confirmPasswordInput?.text.toString()

                // Abilita il bottone solo se entrambe le condizioni sono vere
                if (registerButton != null) {
                    registerButton.isEnabled = allFieldsFilled && passwordsMatch
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Non ci serve implementare questo
            }
        }

        // 4. Applica il TextWatcher a OGNI campo di testo
        // In questo modo, ogni volta che scrivi in QUALSIASI campo, il controllo viene eseguito.
        fields.forEach { editText ->
            editText?.addTextChangedListener(textWatcher)
        }

        // 5. Logica per il click del bottone (opzionale, per dare un feedback)
        registerButton?.setOnClickListener {
            Toast.makeText(this, "Registrazione completata!", Toast.LENGTH_SHORT).show()
        }

        // ========================================================================
        // LOGICA PER L'ESERCIZIO "LISTA INGREDIENTI PIZZA"
        // ========================================================================

        // --- FASE 1: CREAZIONE DELLA LISTA SEMPLICE ---

        // 1. Definiamo la nostra sorgente dati: un array di ingredienti
        val ingredients = arrayOf("Mozzarella", "Pomodoro", "Basilico", "Salame piccante", "Prosciutto cotto",
            "Funghi", "Carciofi", "Olive", "Wurstel", "Gorgonzola"
        )

        // 2. Troviamo i componenti dal layout XML
        val ingredientsListView = findViewById<ListView>(R.id.ingredients_listview)
        val selectionTextView = findViewById<TextView>(R.id.ingredient_selection_textview)
        //creazione dell'array adapter
        //si usa il layout predefinito per una lista semplice.
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients)

        // 4. Colleghiamo l'adapter alla ListView
        if (ingredientsListView != null) {
            ingredientsListView.adapter = arrayAdapter
            
            // --- FASE 2: GESTIRE IL CLICK (SELEZIONE SINGOLA) ---
            //5. Impostiamo un listener per sapere quando un elemento viene cliccato
            ingredientsListView.setOnItemClickListener { parent, view, position, id ->
                // Recuperiamo l'ingrediente cliccato usando la sua posizione
                val selectedIngredient = ingredients[position]
    
                // Aggiorniamo il TextView in alto
                if (selectionTextView != null) {
                    selectionTextView.text = "Scelta: $selectedIngredient"
                }
    
                // Mostriamo un Toast per dare un feedback immediato
                Toast.makeText(this, "Hai cliccato: $selectedIngredient", Toast.LENGTH_SHORT).show()        }
        }


        //Esercizio scelta multipla

        val prodotti = arrayListOf("Pane", "Latte", "Uova", "Farina", "Zucchero", "Caffè", "Mele", "Pasta")
        //Trova  la listview
        val listView: ListView? = findViewById(R.id.myListView)
        val button: Button? = findViewById(R.id.btnCheck)
        
        if (listView != null) {
            //introduzione adapter
            //prendo la lista dal layout standard di android e ci metto l'array di prodotti
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, prodotti)
            //colelgo l'adapter alla lista
            listView.adapter = adapter
        }
        
        //bottone: al click ritorna cosa si è spuntato
        button?.setOnClickListener {
            if (listView != null) {
                //ritorna in una variabile le posizioni in cui sono stpuntati i iprodotti
                val checkPositions = listView.checkedItemPositions
                val selezionati = StringBuilder() // come in java, costruisco una stirnga modulare per creare il messaggio finale
                for (i in 0 until listView.count){
                    //se alla posizione i c'è una spunta...
                    if(checkPositions.get(i)){
                        //...recupero il nome del prodotto
                        val prodottoScelto = prodotti[i]
                        selezionati.append(prodottoScelto).append("\n")
                    }
                }
                //Risultato in un toast
                Toast.makeText(this, "Hai comprato:${selezionati}", Toast.LENGTH_LONG).show()
            }
        }


        //Esercizio Spinner

        // 1. I Dati
        val cities = arrayOf("Roma", "Milano", "Napoli", "Torino", "Firenze", "Palermo", "Venezia")

        // 2. Trova le view
        val spinner: Spinner? = findViewById(R.id.city_spinner)
        val resultText: TextView? = findViewById(R.id.selection_result)

        if (spinner != null) {
            // 3. Crea l'Adapter
            // Nota: usiamo 'simple_spinner_item' per l'aspetto "chiuso"
            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
            // Questo rende le righe più spaziate e facili da cliccare
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 5. Collega l'adapter
            spinner.adapter = spinnerAdapter
           //gestione della selezione
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCity = cities[position]
                    resultText?.text = "Hai scelto: $selectedCity"
                }
    
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //non faccio nulla se l'adapter si svuota (non succede)
                }
            }
        }

        //Esercizio autocomplete view

// 1. Prepariamo un database di province (anche parziale)
        val province = arrayOf(
            "Agrigento", "Alessandria", "Ancona", "Aosta", "Arezzo", "Ascoli Piceno", "Asti", "Avellino",
            "Bari", "Barletta-Andria-Trani", "Belluno", "Benevento", "Bergamo", "Biella", "Bologna", "Bolzano", "Brescia", "Brindisi",
            "Cagliari", "Caltanissetta", "Campobasso", "Carbonia-Iglesias", "Caserta", "Catania", "Catanzaro", "Chieti", "Como", "Cosenza", "Cremona", "Crotone", "Cuneo",
            "Enna",
            "Fermo", "Ferrara", "Firenze", "Foggia", "Forlì-Cesena", "Frosinone",
            "Genova", "Gorizia", "Grosseto",
            "Imperia", "Isernia",
            "L'Aquila", "La Spezia", "Latina", "Lecce", "Lecco", "Livorno", "Lodi", "Lucca",
            "Macerata", "Mantova", "Massa-Carrara", "Matera", "Messina", "Milano", "Modena", "Monza e della Brianza",
            "Napoli", "Novara", "Nuoro",
            "Olbia-Tempio", "Oristano",
            "Padova", "Palermo", "Parma", "Pavia", "Perugia", "Pesaro e Urbino", "Pescara", "Piacenza", "Pisa", "Pistoia", "Pordenone", "Potenza", "Prato",
            "Ragusa", "Ravenna", "Reggio Calabria", "Reggio Emilia", "Rieti", "Rimini", "Roma", "Rovigo",
            "Salerno", "Sassari", "Savona", "Siena", "Siracusa", "Sondrio",
            "Taranto", "Teramo", "Terni", "Torino", "Trapani", "Trento", "Treviso", "Trieste",
            "Udine",
            "Varese", "Venezia", "Verbano-Cusio-Ossola", "Vercelli", "Verona", "Vibo Valentia", "Vicenza", "Viterbo"
        )
        // 2. Troviamo la view
        val autoCompleteTV: AutoCompleteTextView? = findViewById(R.id.autoCompleteProvince)

        if (autoCompleteTV != null) {
            // 3. Creiamo l'Adapter
            // Usiamo un layout semplice di Android per mostrare le righe suggerite
            val autoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, province)
    
            // 4. Colleghiamo l'adapter
            autoCompleteTV.setAdapter(autoAdapter)
        }

        //Esercizio AltertDialog
        val btnExit: Button? = findViewById(R.id.btnExit)
        btnExit?.setOnClickListener {
            //creazione del costruttore con builder
            val builder = AlertDialog.Builder(this)
            //Impostazione del titolo e messaggio
            builder.setTitle("Attenzione")
            builder.setMessage("Sei sicuro di Passare alla prossima pagina dell'app?")

            //tasto positivo ("Si")
            builder.setPositiveButton("Vai alla nuova pagina"){ dialog, which ->
                // 1. Trovo il campo mail (che sta in questa Activity, quindi posso vederlo)
                val etDest: EditText? = findViewById(R.id.dest)

                // 2. Prendo il testo che l'utente ha scritto
                val emailUtente = etDest?.text.toString()

                // 3. Preparo il viaggio
                val intent = Intent(this, PositiveActivity::class.java)

                // 4. CARICO IL PACCO!
                // "CHIAVE_EMAIL" è l'etichetta. emailUtente è il contenuto.
                intent.putExtra("CHIAVE_EMAIL", emailUtente)                 //Ora parte il viaggio!
                startActivity(intent)
            }

            // 5. Tasto NEGATIVO ("No")
            builder.setNegativeButton("No, resta qui") { dialog, which ->
                Toast.makeText(this, "Ottima scelta! Resta con noi.", Toast.LENGTH_SHORT).show()
            }
            // 6. Tasto NEUTRO ("Annulla" / "Boh")
            builder.setNeutralButton("Ci penso") { dialog, which ->
                Toast.makeText(this, "Prenditi il tuo tempo...", Toast.LENGTH_SHORT).show()
                // Non serve fare dialog.dismiss(), si chiude da solo cliccando un tasto
            }
            // 7. FONDAMENTALE: Creare e Mostrare il dialogo
            val dialog = builder.create()
            dialog.show()
        }

        //Esercizio invio mail
        val dest: EditText? = findViewById(R.id.dest)
        val subject: EditText? = findViewById(R.id.subject)
        val body: EditText? = findViewById(R.id.body)
        val btnSendEMail: Button? = findViewById(R.id.btnSendEmail)

        //gestione del blic sul bottone invia
        btnSendEMail?.setOnClickListener {
            if (dest != null && subject != null && body != null) {
                //recupero cio che e stato scritto nei campi
                val destinatario = dest.text.toString()
                val oggetto = subject.text.toString()
    
                //controllo se manca la mail
                if(destinatario.isEmpty()){
                    dest.error = "Inserisci un destinatario!"
                    return@setOnClickListener //si ferma senza aprire il dialog
                }
                //creaizone dell'alert log di conferma
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Conferma Invio")
                //mostro nel messaggio a chi stiamo inviando la mail
                builder.setMessage("Sei sicuro di voler inviare una mail a: \n$destinatario\n con oggetto: '$oggetto'?")
                builder.setIcon(android.R.drawable.ic_dialog_email)
    
                //tasto si:
                builder.setPositiveButton("Si, Invia"){
                    dialog, which ->
                    //creo un intent IMPLICITO per inviare (ACTION_SEND)
                    val emailIntent = Intent(Intent.ACTION_SEND)
                    //SPecifico i dati
                    //mailto: apre solo app di posta
                    emailIntent.data = Uri.parse("mailto:")
                    emailIntent.type = "text/plain"
                    //riempimento dei campi della mail
                    //Nota: EXTRA_MAIL vuole un array nel caso si volesse mandare la mail a piu persone
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, oggetto)
                    //recupero ora anche il corpo della mail
                    val corpoMessaggio = body.text.toString()
                    emailIntent.putExtra(Intent.EXTRA_TEXT, corpoMessaggio)
    
                    //lancio il chooser (menu a scelta)
                    try {
                        // createChooser è elegante: mostra "Scegli un client mail..."
                        startActivity(Intent.createChooser(emailIntent, "Invia mail con..."))
                    } catch (e: Exception){
                        Toast.makeText(this, "Nessuna app di posta trovata!", Toast.LENGTH_SHORT).show()
                    }
                    //pulizia dei campi dopo l'invio
                    dest.text.clear()
                    subject.text.clear()
                    body.text.clear()
                }
    
                //Tasto no:
                builder.setNegativeButton("No, Aspetta"){dialog, which ->
                    //chiude il dialog e non fa nulla
                    dialog.dismiss()
                }
    
                //mostra il dialog
                builder.create().show()
            }
        }

        //Esercizio implicit intent (WEB)
        val url: EditText? = findViewById(R.id.url_edit_text)
        val btnOpenWeb: Button? = findViewById(R.id.ok_button)

        btnOpenWeb?.setOnClickListener {
            if (url != null) {
                //prendi il testo scritto dall'utente
                var urlString = url.text.toString() // Chiamiamola urlString per non confonderci
    
                //piccolo controllo: se l'utente ha scordato http:// ce lo mettiamo noi
                if(!urlString.startsWith("http://") && !urlString.startsWith("https://")){
                    urlString = "http://" + urlString
                }
    
                // --- MANCAVA QUESTA RIGA FONDAMENTALE ---
                // Devi creare l'oggetto 'intent' PRIMA di passarlo al chooser!
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
    
                // Opzionale: Aggiungiamo la categoria BROWSABLE per aiutare il match
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
    
                // Crea esplicitamente il pannello di scelta
                // Qui usiamo la variabile 'intent' che abbiamo appena creato sopra
                val chooser = Intent.createChooser(intent, "Apri con...")
    
                try {
                    startActivity(chooser)
                } catch (e: Exception){
                    Toast.makeText(this, "Nessuna app trovata per aprire il link", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Esercizio implici con condivisione
        val btnShare: Button? = findViewById(R.id.btnShare) // Assicurati di averlo nell'XML

        btnShare?.setOnClickListener {
            // Creo un Intent generico
            val sendIntent = Intent()

            // Dico: "Voglio INVIARE qualcosa"
            sendIntent.action = Intent.ACTION_SEND

            // Dico: "Il contenuto è questo testo"
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Sto imparando Android, è figo!")

            // Dico: "Il tipo di dato è testo semplice"
            sendIntent.type = "text/plain"

            // Creo il menu di scelta (il Chooser) per essere più eleganti
            val shareIntent = Intent.createChooser(sendIntent, "Condividi con...")

            startActivity(shareIntent)
        }
        // --- ASSIGNMENT I ---

        // 1. Gestione del Browser (Ripasso)
        val btnBrowser: Button? = findViewById(R.id.startBrowser)
        btnBrowser?.setOnClickListener {
            // ACTION_VIEW + un link http = Apre il Browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
            startActivity(intent)
        }

        // 2. Gestione del Telefono (NUOVO)
        val btnPhone: Button? = findViewById(R.id.startPhone)
        btnPhone?.setOnClickListener {
            // ACTION_VIEW (o ACTION_DIAL) + un link tel: = Apre il Tastierino
            // Nota il prefisso "tel:" invece di "http:"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:123456789"))
            startActivity(intent)
        }

        //Esercizio 2: gestione delle eccezioni
        //Bottone 1: standard
        findViewById<Button>(R.id.btnViewHttp)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
            startActivity(intent)
        }
        //bottone 2: Azione custom + http (funzionerà perche il manifesto lo riconosce essendo inserito li)
        findViewById<Button>(R.id.btnLaunchHttp)?.setOnClickListener {
            val intent = Intent("com.example.application1.LAUNCH")
            intent.data = Uri.parse("http://www.example.com")
            startActivity(intent)
        }
        //bottone 3: Azioen custom + https -> errore!
        findViewById<Button>(R.id.btnException)?.setOnClickListener {
            //usando https e nel manifesto riconosce solo htttp non trovasndo nessuna app che supporta LAUNCH + https lancerà un eccezione
            val intent = Intent("com.example.application1.LAUNCH")
            intent.data = Uri.parse("https://www.example.com")
            try {
                startActivity(intent)
            } catch (e: Exception){
                //invece di far carshare mostro un avviso personalzziato
                AlertDialog.Builder(this)
                    .setTitle("Errore Gestito")
                    .setMessage("Nessuna app trovata per gestire questa combinazione (LAUNCH + https).\n\nL'app non è crashata grazie al try-catch!")
                    .setPositiveButton("Ok", null)
                    .show()
            }
        }
        // ========================================================================
        // LOGICA PER ASSIGNMENT IV: Custom Messaging
        // ========================================================================

        val smsNumberInput = findViewById<EditText>(R.id.sms_number_input)
        val btnSendTo = findViewById<Button>(R.id.btn_send_sendto)
        val btnSendIntent = findViewById<Button>(R.id.btn_send_intent)
        val btnException = findViewById<Button>(R.id.btn_exception_condition)

        // 1. Bottone "Send sms with SENDTO"
        // Questo è il metodo più specifico per mandare SMS.
        // Usa l'azione ACTION_SENDTO e lo schema "smsto:"
        btnSendTo?.setOnClickListener {
            val number = smsNumberInput?.text.toString()
            val uri = Uri.parse("smsto:$number")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", "Ciao da Esercizio 4 (SENDTO)!")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Nessuna app di messaggi trovata!", Toast.LENGTH_SHORT).show()
            }
        }

        // Questo simula un intento più generico o personalizzato.
        // Nell'esercizio originale si chiede di provare a lanciare un intent che la nostra stessa app
        // potrebbe intercettare se avessimo configurato un intent-filter specifico.
        // Qui usiamo ACTION_VIEW con il tipo MIME degli sms per cercare di aprire un'app di messaggi.
        // 2. Bottone "Send sms with SMS_INTENT"
        btnSendIntent?.setOnClickListener {
            val number = smsNumberInput?.text.toString()

            // Creiamo l'intent
            val intent = Intent(Intent.ACTION_VIEW)

            // INVECE DI: intent.type = "vnd.android-dir/mms-sms"
            // USA QUESTO APPROCCIO PIÙ SICURO:
            intent.data = Uri.parse("sms:$number") // Questo funziona meglio su tutti gli Android

            intent.putExtra("sms_body", "Ciao da Assignment IV (VIEW)!")

            try {
                startActivity(intent)
            } catch (e: Exception) {
                // Stampa l'errore vero nel Logcat per capire cosa succede
                e.printStackTrace()
                Toast.makeText(this, "Errore: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }


        // 3. Bottone "Exception Condition"
        // L'obiettivo qui è far vedere cosa succede se lanci un Intent che NESSUNO sa gestire.
        // L'immagine dell'esercizio mostra un crash ("Unfortunately... has stopped").
        btnException?.setOnClickListener {
            // Creiamo un intent con un'azione completamente inventata
            val intent = Intent("AZIONE_INESISTENTE_CHE_NESSUNO_HA")

            // Se lanciamo startActivity SENZA il blocco try-catch, l'app andrà in crash.
            // Questo è ESATTAMENTE quello che l'esercizio vuole dimostrare.

            // Togli i commenti qui sotto per vedere il crash vero (come nella foto):
            startActivity(intent)

            /*
            // Se invece volessimo gestirlo elegantemente, faremmo così:
            try {
                startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                // Questo codice viene eseguito se non c'è crash
                Toast.makeText(this, "Eccezione catturata! Nessuna app trovata.", Toast.LENGTH_LONG).show()
            }
            */
        }

        // ========================================================================
        // LOGICA PER ESERCIZIO 4 : SmsManager
        // ========================================================================

        val smsManagerNumber = findViewById<EditText>(R.id.sms_manager_number)
        val smsManagerBody = findViewById<EditText>(R.id.sms_manager_body)
        val btnSmsManager = findViewById<Button>(R.id.btn_sms_manager)

        btnSmsManager?.setOnClickListener {
            val numero = smsManagerNumber?.text.toString()
            val messaggio = smsManagerBody?.text.toString()

            if (numero.isEmpty() || messaggio.isEmpty()) {
                Toast.makeText(this, "Inserisci numero e messaggio!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // CONTROLLO PERMESSI (Obbligatorio su Android moderni)
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                // Se non abbiamo il permesso, lo chiediamo
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.SEND_SMS),
                    123 // Codice di richiesta arbitrario
                )
            } else {
                // Se abbiamo già il permesso, inviamo!
                try {
                    val smsManager = android.telephony.SmsManager.getDefault()
                    smsManager.sendTextMessage(numero, null, messaggio, null, null)

                    Toast.makeText(this, "SMS Inviato con successo!", Toast.LENGTH_LONG).show()

                    // Puliamo i campi
                    smsManagerBody?.text?.clear()
                } catch (e: Exception) {
                    Toast.makeText(this, "Errore invio: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }

        // ========================================================================
        // LOGICA PER ESERCIZIO 1: Rubrica
        // ========================================================================

        //1. Creiamo i dati
        val listaContatti = listOf(
            Contatto("Mario", "Rossi", 32, R.drawable.android_button_3d),
            Contatto("Luigi", "Verdi", 25, R.drawable.android_button_3d),
            Contatto("Anna", "Bianchi", 28, R.drawable.android_button_3d),
            Contatto("Giulia", "Neri", 40, R.drawable.android_button_3d)
        )

        // 2. Creiamo l'adapter (mostriamo solo Nome e Cognome nella lista)
        val nomiDaVisualizzare = listaContatti.map { "${it.nome} ${it.cognome}" }
        val adapterRubrica = ArrayAdapter(this, android.R.layout.simple_list_item_1, nomiDaVisualizzare)

        // Nota: uso ? perché in landscape il file xml è cambiato e rubrica_listview c'è,
        // ma se avessi lasciato il vecchio file land vuoto crasherebbe.
        // Con la configurazione attuale c'è in entrambi i file, quindi è sicuro, ma ? è buona prassi.
        val listViewRubrica = findViewById<ListView>(R.id.rubrica_listview)
        listViewRubrica?.adapter = adapterRubrica

        // 3. Gestiamo il Click
        listViewRubrica?.setOnItemClickListener { _, _, position, _ ->
            val contatto = listaContatti[position]

            // TRUCCO MASTER-DETAIL:
            // Cerchiamo una view che esiste SOLO nel layout orizzontale (det_nome)
            val testoNomeLandscape = findViewById<TextView>(R.id.det_nome)

            if (testoNomeLandscape != null) {
                // === SIAMO IN LANDSCAPE (ORIZZONTALE) ===
                // Aggiorniamo la parte destra dello schermo
                testoNomeLandscape.text = contatto.nome
                findViewById<TextView>(R.id.det_cognome)?.text = contatto.cognome
                findViewById<TextView>(R.id.det_eta)?.text = "Età: ${contatto.eta}"
                Toast.makeText(this, "Dettagli aggiornati a destra", Toast.LENGTH_SHORT).show()
            } else {
                // === SIAMO IN PORTRAIT (VERTICALE) ===
                // Apriamo la nuova activity
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("NOME", contatto.nome)
                intent.putExtra("COGNOME", contatto.cognome)
                intent.putExtra("ETA", contatto.eta)
                startActivity(intent)
            }
        }
        // ========================================================================
        // LOGICA PER ESERCIZIO 2: RecyclerView
        // ========================================================================

        // 1. Prepariamo una lista di dati un po' più lunga per vedere l'effetto scorrimento
        val datiRecycler = listOf(
            Contatto("Steve", "Jobs", 56, R.drawable.android_button_3d),
            Contatto("Bill", "Gates", 65, R.drawable.android_button_3d),
            Contatto("Elon", "Musk", 50, R.drawable.android_button_3d),
            Contatto("Linus", "Torvalds", 52, R.drawable.android_button_3d),
            Contatto("Ada", "Lovelace", 36, R.drawable.android_button_3d),
            Contatto("Alan", "Turing", 41, R.drawable.android_button_3d)
        )

        // 2. Troviamo la RecyclerView (usa ? perché in landscape potrebbe non esserci se non l'hai aggiunta)
        val myRecyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_advanced)

        // 3. Configuriamo il LayoutManager (IMPORTANTE: dice "mettile in fila verticale")
        myRecyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        // 4. Creiamo e colleghiamo l'Adapter
        val adapterRecycler = ContactAdapter(datiRecycler) { contattoCliccato ->
            // Qui gestiamo il click sulla riga della RecyclerView
            Toast.makeText(this, "Hai cliccato ${contattoCliccato.nome} dalla RecyclerView!", Toast.LENGTH_SHORT).show()
        }

        myRecyclerView?.adapter = adapterRecycler

        // ========================================================================
        // LOGICA PER ESERCIZIO 3: RecyclerView con CardView
        // ========================================================================

        // 1. Trova la RecyclerView nel layout
        val cardRecyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.card_recycler_view)

        // 2. Imposta il LayoutManager (fondamentale!)
        // Dice alla RecyclerView di disporre gli elementi in una lista verticale.
        cardRecyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        // 3. Prepara i dati da mostrare
        val data = ArrayList<ItemsViewModel>()
        for (i in 1..20) {
            data.add(ItemsViewModel(R.drawable.android_button_3d, "Carta numero " + i))
        }

        // 4. Crea un'istanza del nostro CustomAdapter, passandogli i dati
        val adapter = CustomAdapter(data)

        // 5. Collega l'adapter alla RecyclerView
        cardRecyclerView?.adapter = adapter

        // Esempio: aggiungi questo al fondo del tuo onCreate in MainActivity
        val btnOpenTabs = findViewById<Button>(R.id.btnOpenTabs) // Assicurati di creare questo bottone nell'XML!
        btnOpenTabs?.setOnClickListener {
            val intent = Intent(this, TabActivity::class.java)
            startActivity(intent)
        }
        val btnToDo = findViewById<Button>(R.id.btnOpenToDo) // Crea il bottone nell'XML prima!
        btnToDo.setOnClickListener {
            startActivity(Intent(this, ToDoActivity::class.java))
        }
        val firestoreButton = findViewById<Button>(R.id.btn_open_firestore_exercise)
        firestoreButton.setOnClickListener {
            // Crea un Intent per aprire la nuova Activity
            val intent = Intent(this, FirestoreQuotesActivity::class.java)
            startActivity(intent)
        }


        //Nota: viene chiamato il tost on create ogni volta che si avvia l'app e ogni volta che si ruota lo schermo
    Log.d(TAG, "OnCreate() called")
        Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show()

    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 1. Gestione specifica per il Dark Mode (che non deve mostrare il Toast generico sotto)
        if (item.itemId == R.id.menu_dark_mode) {
            val currentMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
            if (currentMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, "Tema di default impostato su chiaro!", Toast.LENGTH_SHORT).show()
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, "Tema di default impostato su scuro!", Toast.LENGTH_SHORT).show()
            }
            return true
        }

        // 2. Gestione degli altri casi che restituiscono una Stringa
        val messaggio = when (item.itemId) {
            R.id.home -> "Hai selezionato: Home"
            R.id.create_new -> "Hai selezionato: Create New (dal sottomenu)"
            R.id.open -> "Hai selezionato: Open (dal sottomenu)"
            R.id.profilo -> "Hai selezionato: Profilo"
            R.id.impostazioni -> "Hai selezionato: Impostazioni"
            R.id.esci -> "Hai selezionato: Esci"
            R.id.menu_supersize ->{
                val textViewTitolo = findViewById<TextView>(R.id.hello)
                val buttonData = findViewById<Button>(R.id.dateButton)

                // ingreandimento testo e bottone
                if(textViewTitolo != null && buttonData != null) {
                    if (isSupersize){
                        //torna normale
                        textViewTitolo.textSize = 24f
                        buttonData.scaleX = 1.0f
                        buttonData.scaleY = 1.0f

                        isSupersize = false
                        "Tornato alla normalità"
                    } else {
                        textViewTitolo.textSize = 32f
                        buttonData.scaleX = 1.5f
                        buttonData.scaleY = 1.5f
                        "Supersize Me attivato!" // diretto nel messaggio nel toast
                        isSupersize = true
                        "Supersize Me attivato!"
                    }
                }
                else{
                    "Errore: Widget non trovati"
                }

            }
            else -> return super.onOptionsItemSelected(item)
        }

        // 3. Mostra il toast
        if (item.itemId != R.id.menu_file) {
            Toast.makeText(this@MainActivity, messaggio, Toast.LENGTH_SHORT).show()
        }

        // 4. Azione specifica per Esci
        if (item.itemId == R.id.esci) {
            finish()
        }

        return true
    }




    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        Toast.makeText(this, "onResume() called", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
        Toast.makeText(this, "onPause() called", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
        Toast.makeText(this, "onStop() called", Toast.LENGTH_SHORT).show()

    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart() called")
        Toast.makeText(this, "onRestart() called", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
        Toast.makeText(this, "onDestroy() called", Toast.LENGTH_SHORT).show()

    }
//sezione dedicata alle funzioni

fun changeDate() {
val date = findViewById<TextView>(R.id.date)
date?.text=Date().toString()
}

private fun mostraSceltaSelezionata(idSelezionato:Int){
if(idSelezionato==-1) return
val radioButtonScelto = findViewById<android.widget.RadioButton>(idSelezionato)
if (radioButtonScelto != null) {
    android.widget.Toast.makeText(this, "Hai scelto: ${radioButtonScelto.text}", android.widget.Toast.LENGTH_SHORT).show()
}
}

private fun goWeb(){
val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uniupo.it"))
startActivity(browserIntent)
}
}

//creazione di una claaae semplice per mantentere i dati insieme
data class Contatto(
    val nome: String,
    val cognome: String,
    val eta: Int,
    val fotoResId: Int
)