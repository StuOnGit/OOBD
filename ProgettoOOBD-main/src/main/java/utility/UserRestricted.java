package utility;

import data.Dipendente;

// interfaccia per classi con restrezioni relative all'utente
public interface UserRestricted {
    void setLoggedUser(Dipendente loggedUser);
}
