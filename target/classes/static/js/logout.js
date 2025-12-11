// Contenu pour js/logout.js
document.getElementById('logoutBtn').addEventListener('click', function() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => {
            window.location.href = '/login.html';
        });
});