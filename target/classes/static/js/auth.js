document.addEventListener('DOMContentLoaded', function() {

    function redirectToDashboard(role) {
        if (role === 'ADMIN') {
            window.location.href = '/admin.html';
        } else if (role === 'RSSI' || role === 'USER') {
            window.location.href = '/dashbordrssi.html';
        } else if (role === 'AUDITEUR') {
            window.location.href = '/home.html';
        } else {
            console.error('Rôle inconnu, redirection vers login:', role);
            window.location.href = '/login.html';
        }
    }

    function displayError(message) {
        const errorContainer = document.getElementById('errorMessageContainer');
        const errorText = document.getElementById('errorMessageText');
        if (errorContainer && errorText) {
            // Remplacer les sauts de ligne (\n) par des balises <br> pour l'affichage en HTML
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
    // == GESTION DE L'INSCRIPTION (signup.html) - VERSION FINALE SIMPLIFIÉE
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

            fetch('/api/auth/register', {
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

    // === GESTION DE LA CONNEXION (login.html) - CORRIGÉ ===
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();
            hideError();
            const email = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;
            fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            })
            .then(response => {
                if (!response.ok) { return response.json().then(err => { throw err; }); }
                return response.json();
            })
            .then(data => {
                if (data.nextStep) {
                    sessionStorage.setItem('userEmailForOtp', data.email);
                    if (data.nextStep === 'SETUP_OTP') {
                        sessionStorage.setItem('qrCodeUrl', data.qrCodeImage);
                        window.location.href = '/qrcode.html';
                    } else if (data.nextStep === 'VERIFY_OTP') {
                        window.location.href = '/verify-otp.html';
                    }
                } else {
                    throw new Error(data.message || 'Réponse inattendue du serveur.');
                }
            })
            .catch(error => {
                console.error('Erreur technique:', error.message);
                // On affiche le message d'erreur spécifique du backend
                // Mais pour le login, il est plus sûr d'afficher un message générique
                displayError('L\'adresse email ou le mot de passe est incorrect.');
            });
        });
    }

    // === GESTION DE LA CONFIGURATION (qrcode.html) ===
    const verifyAndSetupOtpBtn = document.getElementById('verifyAndSetupOtpBtn');
    if (verifyAndSetupOtpBtn) {
        const imageUrl = sessionStorage.getItem('qrCodeUrl');
        const email = sessionStorage.getItem('userEmailForOtp');
        if (imageUrl && email) {
            document.getElementById('qrCodeImage').src = imageUrl;
        } else {
            window.location.href = '/login.html';
        }

        verifyAndSetupOtpBtn.addEventListener('click', function() {
            hideError();
            const otpCode = parseInt(document.getElementById('otpCode').value, 10);
            const qrCodeFileName = imageUrl.split('/').pop();

            fetch('/api/auth/verify-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, otpCode, qrCodeFileName })
            })
            .then(response => {
                if (!response.ok) { return response.json().then(err => { throw err; }); }
                return response.json();
            })
            .then(data => {
                if (data.nextStep === 'HOME' && data.role) {
                    sessionStorage.clear();
                    redirectToDashboard(data.role);
                } else {
                    throw new Error(data.message || 'Code invalide ou token manquant.');
                }
            })
            .catch(error => {
                console.error('Erreur technique de connexion OTP:', error.message);
                displayError('Le code de vérification est invalide. Veuillez réessayer.');
            });
        });
    }

    // === GESTION DE LA VÉRIFICATION (verify-otp.html) ===
    const loginWithOtpBtn = document.getElementById('loginWithOtpBtn');
    if (loginWithOtpBtn) {
        const email = sessionStorage.getItem('userEmailForOtp');
        if (!email) {
            console.error("Session expirée, redirection vers login.");
            window.location.href = '/login.html';
        }

        loginWithOtpBtn.addEventListener('click', function() {
            hideError();
            const otpCode = parseInt(document.getElementById('otpCode').value, 10);

            fetch('/api/auth/login-with-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, otpCode })
            })
            .then(response => {
                if (!response.ok) { return response.json().then(err => { throw err; }); }
                return response.json();
            })
            .then(data => {
                if (data.nextStep === 'HOME' && data.role) {
                    sessionStorage.clear();
                    redirectToDashboard(data.role);
                } else {
                    throw new Error(data.message || 'Code invalide ou token manquant.');
                }
            })
            .catch(error => {
                displayError('Le code de vérification est invalide. Veuillez réessayer.');
            });
        });
    }
});