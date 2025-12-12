package com.example.application1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Questa Activity usa SOLO Compose. Niente setContentView(R.layout...)
class ComposePlaygroundActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Qui inizia il mondo Compose
            MaterialTheme {
                SchermataDeiTest()
            }
        }
    }
}
@Preview
@Composable
fun SchermataDeiTest() {
    // Scaffold è lo scheletro della schermata (TopBar, Background, ecc.)
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Laboratorio Compose") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        // LazyColumn è la nuova RecyclerView: una lista che scorre
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Spazio tra gli elementi
        ) {
            // Qui aggiungiamo i nostri "Test" uno per uno

            item {
                CardTest(titolo = "1. Testo Semplice", descrizione = "Il componente base per scrivere") {
                    Text("Ciao! Questo è un testo in Compose.", color = Color.Blue)
                }
            }

            item {
                CardTest(titolo = "2. Bottone Interattivo", descrizione = "Gestione dello stato (click)") {
                    // Esempio di STATO: una variabile che ricorda un numero
                    var conteggio by remember { mutableIntStateOf(0) }

                    Button(onClick = { conteggio++ }) {
                        Text("Cliccami! Volte: $conteggio")
                    }
                }
            }

            item {
                CardTest(titolo = "3. Righe e Colonne", descrizione = "Layout orizzontale e verticale") {
                    Column {
                        Text("Riga 1")
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Sinistra")
                            Text("Destra")
                        }
                    }
                }
            }
            item {
                CardTest(
                    titolo = "4. Profilo Utente",
                    descrizione = "Uso di Row e Column insieme"
                ) {
                    //si chiama la funzione
                    TestProfiloUtente()
                }
            }
        }
    }
}

// Questo è un componente "Helper" che ho creato per rendere belli i test
// Crea una Card grigia con Titolo, Descrizione e il contenuto del test
@Composable
fun CardTest(titolo: String, descrizione: String, contenutoTest: @Composable () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = titolo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = descrizione, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

            // Linea divisoria
            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

            // Qui viene inserito il componente che stiamo testando
            contenutoTest()
        }
    }
}
@Preview
@Composable
fun TestProfiloUtente(){
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        //Icona
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .background(Color.Gray, shape = CircleShape)
                .padding(8.dp),
                tint = Color.White
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column{
            Text("Mario Rossi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Sviluppatore Android", color = Color.Gray, fontSize = 14.sp)
        }
    }
}
