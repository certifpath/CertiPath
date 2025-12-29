document.addEventListener('DOMContentLoaded', function() {
    const logoutButton = document.getElementById('logoutBtn');

    // Function to perform logout
    function performLogout() {
        // 1. Clear Client Data
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        sessionStorage.clear();

        // 2. Redirect
        window.location.href = '/login.html';
    }

    if (logoutButton) {
        logoutButton.addEventListener('click', function(e) {
            e.preventDefault();
            performLogout();

            // Optional: Call backend to blacklist token if implemented
            // fetch('http://localhost:8080/auth/logout', { method: 'POST' });
        });
    }
});