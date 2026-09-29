package com.example.mobilelabs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilelabs.ui.theme.MobileLabsTheme

data class Character(
    val name: String,
    val species: String,
    val status: String,
    val location: String
)

private val characters = listOf(
    Character(
        "Rick Sanchez",
        "Human",
        "Alive",
        "Earth (C-137)"
    ),
    Character(
        "Morty Smith",
        "Human",
        "Alive",
        "Earth (C-137)"
    ),
    Character(
        "Summer Smith",
        "Human",
        "Alive",
        "Earth (C-137)"
    ),
    Character(
        "Beth Smith",
        "Human",
        "Alive",
        "Earth (C-137)"
    ),
    Character(
        "Jerry Smith",
        "Human",
        "Alive",
        "Earth (C-137)"
    ),
    Character(
        "Mr. Meeseeks",
        "Humanoid",
        "Alive",
        "Earth (Replacement Dimension)"
    ),
    Character(
        "Birdperson",
        "Alien",
        "Alive",
        "Bird World"
    ),
    Character(
        "Squanchy",
        "Alien",
        "Alive",
        "Unknown"
    ),
    Character(
        "Evil Morty",
        "Human",
        "Alive",
        "Unknown"
    ),
    Character(
        "Pickle Rick",
        "Human",
        "Alive",
        "Earth (C-137)"
    )
)

class ContentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileLabsTheme {
                ContentScreen(
                    onSettingsClick = {
                        val intent = Intent(
                            this@ContentActivity,
                            SettingsActivity::class.java
                        )

                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun ContentScreen(
    onSettingsClick: () -> Unit
) {

    val backgroundColor = Color(0xFF101118)
    val greenColor = Color(0xFF97CE4C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "RICK & MORTY",
                    color = greenColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Characters",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = onSettingsClick
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = greenColor
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(characters) { character ->

                CharacterCard(
                    character = character
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: Character
) {

    val greenColor = Color(0xFF97CE4C)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B1D26)
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = character.name,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Species: ${character.species}",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Text(
                text = "Status: ${character.status}",
                color = greenColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Location: ${character.location}",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}