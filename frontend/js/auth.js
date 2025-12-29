// Point to your Spring Cloud Gateway
const API_BASE_URL = 'http://localhost:8080';

document.addEventListener('DOMContentLoaded', function() {

    function redirectToDashboard(role) {
        if (role === 'ADMIN') {
            window.location.href = '/admin.html';
        } else if (role === 'RSSI' || role === 'USER') {
            // FIX: Name matches your file structure
            window.location.href = '/rssi-dashboard.html';
        } else if (role === 'AUDITEUR') {
            // FIX: Name matches your file structure
            window.location.href = '/auditeur.html';
        } else {
            console.error('Rôle inconnu, redirection vers login:', role);
            window.location.href = '/login.html';
        }
    }

    function displayError(message) {
        const errorContainer = document.getElementById('errorMessageContainer');
        const errorText = document.getElementById('errorMessageText');
        if (errorContainer && errorText) {
            errorText.innerHTML = message.replace(/\n/g, '<br>');
            errorContainer.style.display = 'flex';
        }
    }

    function hideError() {
        const errorContainer = document.getElementById('errorMessageContainer');
        if (errorContainer) {
            errorContainer.style.display = 'none';
        }
    }

    // =======================================================
    // REGISTER
    // =======================================================
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', function(event) {
            event.preventDefault();
            hideError();

            const registerData = {
                name: document.getElementById('firstName').value,
                lastName: document.getElementById('lastName').value,
                company: document.getElementById('company').value,
                email: document.getElementById('email').value,
                password: document.getElementById('password').value,
                role: document.getElementById('role').value
            };

            // FIX: Use Gateway URL
            fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(registerData)
            })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorBody => { throw { status: response.status, body: errorBody }; });
                }
                return response.json();
            })
            .then(data => {
                window.location.href = '/login.html';
            })
            .catch(error => {
                            console.error('Erreur interceptée:', error);

                            // --- NOUVELLE LOGIQUE D'AFFICHAGE SIMPLIFIÉE ---
                            if (error && error.body) {
                                const errorBody = error.body;

                                if (errorBody.message) {
                                    // Cas 1: Erreur métier générale (ex: "Email already exists")
                                    displayError(errorBody.message);
                                } else {
                                    // Cas 2: Erreurs de validation de champ
                                    // On extrait tous les messages d'erreur de l'objet
                                    const errorMessages = Object.values(errorBody);

                                    // On les joint avec un caractère de saut de ligne
                                    const formattedMessage = errorMessages.join('\n');

                                    // On affiche le tout
                                    displayError(formattedMessage);
                                }
                            } else {
                                // Cas 3: Erreur inattendue (réseau, etc.)
                                displayError('Une erreur réseau est survenue. Veuillez réessayer.');
                            }
                        });
        });
    }

    // =======================================================
    // LOGIN (STEP 1)
    // =======================================================
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();
            hideError();
            const email = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;

            // FIX: Use Gateway URL
            fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            })
            .then(response => {
                if (!response.ok) { return response.json().then(err => { throw err; }); }
                return response.json();
            })
            .then(data => {
                // If backend asks for OTP
                if (data.nextStep) {
                    sessionStorage.setItem('userEmailForOtp', data.email);
                    if (data.nextStep === 'SETUP_OTP') {
                        sessionStorage.setItem('qrCodeUrl', data.qrCodeImage);
                        window.location.href = '/qrcode.html';
                    } else if (data.nextStep === 'VERIFY_OTP') {
                        window.location.href = '/verify-otp.html';
                    }
                }
                // If backend logs in directly (MFA disabled)
                else if (data.token) {
                    localStorage.setItem('authToken', data.token); // IMPORTANT: Save Token
                    localStorage.setItem('userRole', data.role);
                    redirectToDashboard(data.role);
                }
            })
            .catch(error => {
                console.error('Erreur technique:', error);
                displayError('L\'adresse email ou le mot de passe est incorrect.');
            });
        });
    }

    // =======================================================
    // OTP SETUP (STEP 2a)
    // =======================================================
    const verifyAndSetupOtpBtn = document.getElementById('verifyAndSetupOtpBtn');
    if (verifyAndSetupOtpBtn) {
        const imageUrl = sessionStorage.getItem('qrCodeUrl');
        const email = sessionStorage.getItem('userEmailForOtp');

        // Fix: Ensure Image loads from Gateway if it's a relative path
        if (imageUrl && email) {
            const finalImgUrl = imageUrl.startsWith('http') ? imageUrl : `${API_BASE_URL}${imageUrl}`;
            document.getElementById('qrCodeImage').src = finalImgUrl;
        } else {
            window.location.href = '/login.html';
        }

        verifyAndSetupOtpBtn.addEventListener('click', function() {
            hideError();
            const otpCode = parseInt(document.getElementById('otpCode').value, 10);

            // Backend endpoint name must match AuthController
            fetch(`${API_BASE_URL}/auth/verify-otp-setup`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, otpCode })
            })
            .then(response => {
                if (!response.ok) { return response.json().then(err => { throw err; }); }
                return response.json();
            })
            .then(data => {
                if (data.nextStep === 'HOME' && data.token) {
                    sessionStorage.clear(); // Clear OTP temp data

                    // CRITICAL: Save the Token & Role
                    localStorage.setItem('authToken', data.token);
                    localStorage.setItem('userRole', data.role);

                    redirectToDashboard(data.role);
                } else {
                    throw new Error(data.message || 'Code invalide.');
                }
            })
            .catch(error => {
                displayError('Le code de vérification est invalide.');
            });
        });
    }

    // =======================================================
    // OTP VERIFY (STEP 2b)
    // =======================================================
    const loginWithOtpBtn = document.getElementById('loginWithOtpBtn');
    if (loginWithOtpBtn) {
        const email = sessionStorage.getItem('userEmailForOtp');
        if (!email) {
            window.location.href = '/login.html';
        }

        loginWithOtpBtn.addEventListener('click', function() {
            hideError();
            const otpCode = parseInt(document.getElementById('otpCode').value, 10);

            // FIX: Use the correct endpoint for Login
            // WAS: fetch(`${API_BASE_URL}/auth/verify-otp`, ...)
            fetch(`${API_BASE_URL}/auth/login-with-otp`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, otpCode })
            })
            .then(response => {
                if (!response.ok) { return response.json().then(err => { throw err; }); }
                return response.json();
            })
            .then(data => {
                if (data.nextStep === 'HOME' && data.token) {
                    sessionStorage.clear();
                    localStorage.setItem('authToken', data.token);
                    localStorage.setItem('userRole', data.role);
                    redirectToDashboard(data.role);
                } else {
                    throw new Error(data.message || 'Code invalide.');
                }
            })
            .catch(error => {
                displayError('Le code de vérification est invalide.');
            });
        });
    }
});