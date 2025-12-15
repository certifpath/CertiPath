// Contenu final et complet pour js/guard.js avec gestion des rôles

(function() {
    const currentPage = window.location.pathname;

    // Définition des pages et des rôles autorisés
    const pageRoles = {
        '/home.html': ['AUDITEUR'],
        '/dashbordrssi.html': ['RSSI', 'USER'],
        '/admin.html': ['ADMIN']
    };

    const authFlowPages = ['/qrcode.html', '/verify-otp.html'];
    if (authFlowPages.includes(currentPage)) {
        // Logique pour les pages de transition (ne change pas)
        const userEmail = sessionStorage.getItem('userEmailForOtp');
        if (!userEmail) {
            window.location.href = '/login.html';
        }
        return;
    }

    // Fonction pour rediriger l'utilisateur vers son dashboard par défaut
    function redirectToDefaultDashboard(role) {
        if (role === 'ADMIN') { window.location.href = '/admin.html'; }
        else if (role === 'RSSI' || role === 'USER') { window.location.href = '/dashbordrssi.html'; }
        else if (role === 'AUDITEUR') { window.location.href = '/home.html'; }
        else { window.location.href = '/login.html'; }
    }

    fetch('/api/users/me')
        .then(response => {
            if (!response.ok) { throw new Error('Non authentifié'); }
            return response.json();
        })
        .then(user => {
            // L'utilisateur est bien authentifié, nous avons ses informations, y compris son rôle.
            const userRole = user.role;
            
            // On récupère les rôles autorisés pour la page actuelle
            const allowedRoles = pageRoles[currentPage];

            if (allowedRoles && !allowedRoles.includes(userRole)) {
                // L'utilisateur est authentifié, mais il n'a PAS le bon rôle pour voir CETTE page.
                // On le redirige vers son propre tableau de bord.
                console.warn(`Accès non autorisé au rôle ${userRole} pour la page ${currentPage}. Redirection...`);
                redirectToDefaultDashboard(userRole);
            } else {
                // L'utilisateur est authentifié ET a le bon rôle. On le laisse sur la page.
                const userNameElement = document.getElementById('userName');
                if (userNameElement) {
                    userNameElement.textContent = user.name;
                }
                console.log(`Accès autorisé pour ${user.email} (Rôle: ${userRole})`);
            }
        })
        .catch(error => {
            // L'utilisateur n'est pas du tout authentifié.
            console.error('Accès non autorisé, redirection vers login.');
            window.location.href = '/login.html';
        });

})();