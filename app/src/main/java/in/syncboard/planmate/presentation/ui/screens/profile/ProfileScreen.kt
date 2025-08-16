// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/profile/ProfileScreen.kt

package `in`.syncboard.planmate.presentation.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.ui.components.CustomTextField
import `in`.syncboard.planmate.presentation.viewmodel.ProfileViewModel
import `in`.syncboard.planmate.ui.theme.*

/**
 * Profile Menu Item Data Class
 */
data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color,
    val onClick: () -> Unit
)

/**
 * Profile Screen - Updated with real user data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val editState = viewModel.editProfileState
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Show loading state
    if (uiState.isLoading) {
        LoadingState(message = "Loading profile...")
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!editState.isEditing) {
                        IconButton(onClick = { viewModel.startEditingProfile() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Profile Header Card
            item {
                if (editState.isEditing) {
                    EditProfileCard(
                        editState = editState,
                        onNameChanged = { viewModel.updateName(it) },
                        onEmailChanged = { viewModel.updateEmail(it) },
                        onPhoneChanged = { viewModel.updatePhone(it) },
                        onSave = { viewModel.saveProfile() },
                        onCancel = { viewModel.cancelEditing() }
                    )
                } else {
                    ProfileHeaderCard(
                        user = uiState.currentUser,
                        stats = uiState.userStats
                    )
                }
            }

            // Stats Cards (only show when not editing)
            if (!editState.isEditing) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Expenses
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = CategoryCardShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Primary50
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = uiState.userStats.totalExpenses.toString(),
                                    style = MaterialTheme.typography.amountMedium,
                                    color = Primary700,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Expenses",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Primary600
                                )
                            }
                        }

                        // Money Saved
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = CategoryCardShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Tertiary50
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "₹${String.format("%,.0f", uiState.userStats.moneySaved)}",
                                    style = MaterialTheme.typography.amountMedium,
                                    color = Tertiary700,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Saved",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tertiary600
                                )
                            }
                        }

                        // Account Age
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = CategoryCardShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Secondary50
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = uiState.userStats.accountAge,
                                    style = MaterialTheme.typography.amountMedium,
                                    color = Secondary700,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Member",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Secondary600
                                )
                            }
                        }
                    }
                }

                // Menu Items
                item {
                    Text(
                        text = "Account & Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Profile Menu Items
                item {
                    val menuItems = listOf(
                        ProfileMenuItem(
                            icon = Icons.Default.CreditCard,
                            title = "Payment Methods",
                            subtitle = "Manage cards and payment accounts",
                            color = Tertiary500,
                            onClick = { /* Handle payment methods */ }
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.TrendingUp,
                            title = "Financial Goals",
                            subtitle = "Track and manage your financial goals",
                            color = Secondary500,
                            onClick = { /* Handle financial goals */ }
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            subtitle = "Customize your alert preferences",
                            color = Warning500,
                            onClick = { /* Handle notifications */ }
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.Security,
                            title = "Security & Privacy",
                            subtitle = "Manage your account security",
                            color = Error500,
                            onClick = { /* Handle security */ }
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.Settings,
                            title = "App Settings",
                            subtitle = "Customize your app experience",
                            color = Neutral70,
                            onClick = { /* Handle app settings */ }
                        )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        menuItems.forEach { item ->
                            ProfileMenuItemCard(item = item)
                        }
                    }
                }

                // Support Section
                item {
                    Text(
                        text = "Support",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    val supportItems = listOf(
                        ProfileMenuItem(
                            icon = Icons.Default.Help,
                            title = "Help & Support",
                            subtitle = "Get help and contact support",
                            color = Primary500,
                            onClick = { /* Handle help */ }
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.Feedback,
                            title = "Send Feedback",
                            subtitle = "Help us improve PlanMate",
                            color = Tertiary500,
                            onClick = { /* Handle feedback */ }
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.Info,
                            title = "About PlanMate",
                            subtitle = "Version 1.0.0",
                            color = Secondary500,
                            onClick = { /* Handle about */ }
                        )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        supportItems.forEach { item ->
                            ProfileMenuItemCard(item = item)
                        }
                    }
                }

                // Logout Button
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Error500
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Error500)
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Sign Out",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Error Message
            if (uiState.errorMessage != null || editState.errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Error50
                        )
                    ) {
                        Text(
                            text = uiState.errorMessage ?: editState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Error700,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    "Sign Out",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text("Are you sure you want to sign out? You'll need to sign in again to access your account.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout {
                            onLogout()
                        }
                    }
                ) {
                    Text(
                        "Sign Out",
                        color = Error500,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Profile Header Card Component
 */
@Composable
private fun ProfileHeaderCard(
    user: `in`.syncboard.planmate.domain.entity.User?,
    stats: `in`.syncboard.planmate.presentation.viewmodel.ProfileStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardLargeShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Secondary500, Primary500)
                    ),
                    shape = CardLargeShape
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.name?.firstOrNull()?.toString()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Name
                Text(
                    text = user?.name ?: "User",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                // Email
                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                // Phone (if available)
                user?.phoneNumber?.let { phone ->
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Member Since
                Text(
                    text = "Member for ${stats.accountAge}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Edit Profile Card Component
 */
@Composable
private fun EditProfileCard(
    editState: `in`.syncboard.planmate.presentation.viewmodel.EditProfileUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardLargeShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = editState.name,
                onValueChange = onNameChanged,
                label = "Name",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = editState.email,
                onValueChange = onEmailChanged,
                label = "Email",
                leadingIcon = Icons.Default.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = editState.phone,
                onValueChange = onPhoneChanged,
                label = "Phone Number",
                leadingIcon = Icons.Default.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    enabled = !editState.isSaving
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    enabled = !editState.isSaving && editState.name.isNotBlank() && editState.email.isNotBlank()
                ) {
                    Text(if (editState.isSaving) "Saving..." else "Save")
                }
            }
        }
    }
}

/**
 * Profile Menu Item Card Component
 */
@Composable
private fun ProfileMenuItemCard(
    item: ProfileMenuItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = item.onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = item.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go to ${item.title}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}