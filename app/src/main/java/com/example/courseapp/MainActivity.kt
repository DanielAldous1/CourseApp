package com.example.courseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    CourseScreen()
                }
            }
        }
    }
}

@Composable
fun CourseScreen(vm: CourseViewModel = viewModel()) {
    val courses by vm.courses.collectAsState()
    val selected by vm.selected.collectAsState()

    var dept by rememberSaveable { mutableStateOf("") }
    var number by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }

    // Load selected course into fields (EDIT behavior) — avoids smart-cast issues
    LaunchedEffect(selected?.id) {
        selected?.let {
            dept = it.department
            number = it.number
            location = it.location
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Course Viewer & Editor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        // Add / Edit form
        Card {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    if (selected == null) "Add a course" else "Edit course",
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = dept,
                    onValueChange = { dept = it },
                    label = { Text("Department (e.g., CS)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Course Number (e.g., 4530)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (e.g., WEB 105)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val s = selected
                            if (s == null) {
                                vm.addCourse(dept, number, location)
                            } else {
                                vm.updateCourse(s.id, dept, number, location)
                                vm.clearSelection()
                            }
                            dept = ""
                            number = ""
                            location = ""
                        }
                    ) {
                        Text(if (selected == null) "Save" else "Update")
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Course list
            Card(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize().padding(12.dp)) {
                    Text("Courses", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(courses) { course ->
                            CourseRow(
                                course = course,
                                onClick = { vm.select(course) },
                                onDelete = { vm.deleteCourse(course) }
                            )
                        }
                    }
                }
            }

            // Details
            Card(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Details", fontWeight = FontWeight.Medium)

                    selected?.let { s ->
                        Text("Department: ${s.department}")
                        Text("Number: ${s.number}")
                        Text("Location: ${s.location}")

                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(onClick = {
                            vm.clearSelection()
                            dept = ""
                            number = ""
                            location = ""
                        }) {
                            Text("Clear")
                        }
                    } ?: run {
                        Text("Tap a course to view or edit details.")
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseRow(
    course: Course,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = course.name,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}
