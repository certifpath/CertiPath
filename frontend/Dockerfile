FROM nginx:alpine

# Copier les fichiers HTML/CSS/JS dans le dossier par défaut de Nginx
COPY . /usr/share/nginx/html

# Copier une config Nginx personnalisée (optionnel mais recommandé pour le SPA)
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]