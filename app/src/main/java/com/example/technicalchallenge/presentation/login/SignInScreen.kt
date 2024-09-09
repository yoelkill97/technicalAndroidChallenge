package com.example.technicalchallenge.presentation.login

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.technicalchallenge.R
import com.example.technicalchallenge.presentation.components.CodeValidationDialog
import com.example.technicalchallenge.presentation.components.CustomTextField
import com.example.technicalchallenge.presentation.components.LoadingScreen
import com.example.technicalchallenge.presentation.components.PhoneInputCode
import com.example.technicalchallenge.util.boldTitleStyle
import com.example.technicalchallenge.util.colorBackground
import com.example.technicalchallenge.util.colorMediumBlue
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Preview
@Composable
fun SingInPreview() {
    SignInScreen()
}

@Composable
fun SingInPage( onNavigateToRegister: () -> Unit,
    viewModel: LoginScreenViewModel = hiltViewModel(),
) {
    val loadingState = viewModel.loadingState.collectAsState()
    SignInScreen(onNavigateToRegister = onNavigateToRegister ,loadingState = loadingState.value, onLogin = { viewModel.signWhitFirebase(it) },onLoadingChange = {viewModel.loadingStateChange(it)})
}

@Composable
fun SignInScreen(
    onNavigateToRegister: () -> Unit = {},
    loadingState: LoadingState = LoadingState.IDLE,
    onLogin: (AuthCredential) -> Unit = {},
    context: Context = LocalContext.current,
    onLoadingChange: (Boolean) -> Unit = {},
) {
    val callbackManager = remember { CallbackManager.Factory.create() }
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val googleSignInClient = remember { getGoogleSignInClient(context) }
    var showModalValidationPhone by remember { mutableStateOf(false) }
    var codeInput by remember { mutableStateOf("") }
    var storedVerificationId by remember { mutableStateOf("") }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    val scope = rememberCoroutineScope()


    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            callbackManager.onActivityResult(it.resultCode, it.resultCode, it.data)
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                onLogin( credential)
            } catch (e: ApiException) {
                // Handle error
                Log.d("TAG", "firebaseAuthWithGoogle: ${e.localizedMessage}")
            }

        }
    LaunchedEffect(key1 = loadingState) {
        when (val state = loadingState) {
            is LoadingState.SUCCESS -> {
                scope.launch {
                    onNavigateToRegister()
                    Log.d(
                        "TAG",
                        "firebaseAuthWithFacebook: ${state.authResult.user?.displayName}"
                    )
                    Toast.makeText(
                        context,
                        "Sign In ${state.authResult.user?.displayName}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            is LoadingState.ERROR -> {

                scope.launch {
                    Toast.makeText(context, "Error: ${state.error}", Toast.LENGTH_LONG).show()
                }
            }

            else -> {
            }
        }
    }
    fun startPhoneVerification(phoneNumber: String) {
         onLoadingChange(true)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    onLoadingChange(false)
                    Log.d("PhoneAuth", "onVerificationCompleted:$credential")
                    onLogin(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onLoadingChange(false)
                    Log.w("PhoneAuth", "onVerificationFailed", e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    Log.d("PhoneAuth", "onCodeSent:$verificationId")
                    onLoadingChange(false)
                    storedVerificationId = verificationId
                    resendToken = token
                    showModalValidationPhone = true

                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun loginManagerFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
            context as Activity, listOf("email", "public_profile")
        )
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                override fun onCancel() {

                    Log.d("TAG", "Facebook Login Canceled")
                }

                override fun onError(error: FacebookException) {

                    Log.d("TAG", "Facebook Login Error")
                }

                override fun onSuccess(result: LoginResult) {

                    val credential =
                        FacebookAuthProvider.getCredential(result.accessToken.token )
                    onLogin(credential)
                }


            })
    }
    /*    val facebookLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val result = FacebookSdk.getCallbackManager().onActivityResult(it.resultCode, it.data)
            if (result) {
                // Handle Facebook login result
                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken != null && !accessToken.isExpired) {
                    val credential = FacebookAuthProvider.getCredential(accessToken.token)
                    viewModel.signWhitFacebookCredential(credential) {
                        Log.d("TAG", "firebaseAuthWithFacebook: ${accessToken.userId}")
                    }
                }
            }
        }*/

    Column(
        modifier = Modifier
            .background(colorBackground)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_android),
            contentDescription = null,
            modifier = Modifier.size(220.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.18f)
        )
        //PhoneVerificationButton(viewModel, auth)
        PhoneInputCode(onVerificationCode = {   startPhoneVerification(it)})
        CodeValidationDialog(
            codeInput = codeInput,
            isShowingDialog = showModalValidationPhone,
            onDismiss = { showModalValidationPhone = false },
            onCodeChange = { codeInput = it },
            onValidate = { code ->
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
                onLogin(credential)
            },)
        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Sign-In",
                    tint = Color.Unspecified, // Sin tint
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Sign in with Google",
                    color = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Facebook Sign-In Button
        Button(
            onClick = { loginManagerFacebook() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1877F2)
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "Facebook Sign-In",
                    tint = Color.Unspecified, // Sin tint
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Sign in with Facebook",
                    color = Color.White // Texto en blanco
                )
            }
        }
    }
    LoadingScreen(isShowingDialog = loadingState is LoadingState.LOADING)


}

// Function to get Google Sign-In Client
fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}
