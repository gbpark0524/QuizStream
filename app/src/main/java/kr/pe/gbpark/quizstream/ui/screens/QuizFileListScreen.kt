package kr.pe.gbpark.quizstream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kr.pe.gbpark.quizstream.data.QuizFile
import kr.pe.gbpark.quizstream.data.QuizRepository
import kr.pe.gbpark.quizstream.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizFileListScreen(
    onQuizSelected: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { QuizRepository(context) }
    
    var quizFiles by remember { mutableStateOf<List<QuizFile>>(emptyList()) }
    var selectedQuizFile by remember { mutableStateOf<QuizFile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // 앱 시작 시 퀴즈 파일 목록 로드
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // 먼저 assets의 파일들을 files로 복사
                repository.copyAssetsToFiles()
                // 그 다음 파일 목록 로드
                quizFiles = repository.loadQuizFiles()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "QuizStream",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceWhite
            )
        )
        
        // Quiz Files Section
        Text(
            text = "Quiz Files",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        )
        
        // Loading or Quiz Files List
        if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Blue500)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quizFiles) { quizFile ->
                    QuizFileItem(
                        quizFile = quizFile,
                        isSelected = selectedQuizFile?.id == quizFile.id,
                        onClick = { 
                            selectedQuizFile = quizFile
                        }
                    )
                }
            }
        }
        
        // Start Quiz Button
        Box(
            modifier = Modifier.padding(16.dp).navigationBarsPadding()
        ) {
            Button(
                onClick = { 
                    selectedQuizFile?.let { onQuizSelected(it.id) }
                },
                enabled = selectedQuizFile != null && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue600,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Start Quiz",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuizFileItem(
    quizFile: QuizFile,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Blue500 else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Blue600 else Color(0xFFF5F5F5)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = if (isSelected) TextOnBlue else TextSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // File Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = quizFile.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) TextOnBlue else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${quizFile.questionCount} questions",
                    fontSize = 14.sp,
                    color = if (isSelected) Blue100 else TextSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizFileListScreenPreview() {
    QuizStreamTheme {
        QuizFileListScreen()
    }
}