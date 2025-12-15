document.addEventListener('DOMContentLoaded', function() {
    const logoutButton = document.getElementById('logoutBtn');
    if (logoutButton) {
        logoutButton.addEventListener('click', function() {
            fetch('/api/auth/logout', { method: 'POST' })
                .then(() => {
                    // Après la déconnexion, on redirige vers la page de login.
                    window.location.href = '/login.html';
                })
                .catch(error => {
                    console.error('Erreur lors de la déconnexion:', error);
                    // Même en cas d'erreur, on redirige.
                    window.location.href = '/login.html';
                });
        });
    }
});