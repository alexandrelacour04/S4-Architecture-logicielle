const bcrypt = require('bcrypt');

// Mot de passe en clair
const plainPassword = 'But2R041';

// Générer un sel et encoder le mot de passe
const saltRounds = 10; // mêmes paramètres que dans bcrypt utilisé
bcrypt.hash(plainPassword, saltRounds, function (err, hash) {
    if (err) {
        console.error("Erreur lors de l'encodage du mot de passe :", err);
    } else {
        console.log("Mot de passe encodé :", hash);
    }
});