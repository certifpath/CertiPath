(function() {
    fetch('/api/users/me')
        .then(response => {
            if (!response.ok) {
                // Si la réponse n'est pas OK (401, 403), le cookie est invalide ou absent.
                // On lève une erreur pour passer au bloc .catch().
                throw new Error('Non authentifié');
            }
            // Si la réponse est OK, on la laisse se poursuivre (on pourrait utiliser les données ici).
            return response.json();
        })
        .then(user => {
            // L'utilisateur est authentifié. On peut optionnellement personnaliser la page.
            // Par exemple, si un élément avec l'ID 'userName' existe, on y met le nom.
            const userNameElement = document.getElementById('userName');
            if (userNameElement) {
                userNameElement.textContent = user.name;
            }
            console.log('Accès autorisé pour:', user.email);
        })
        .catch(error => {
            // La requête a échoué, l'utilisateur n'est pas authentifié.
            console.error('Accès non autorisé, redirection vers login.');
            window.location.href = '/login.html';
        });
})();