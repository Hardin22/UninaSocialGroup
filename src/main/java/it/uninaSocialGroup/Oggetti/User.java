package it.uninaSocialGroup.Oggetti;

import java.time.LocalDate;

public class User {
    private String nomeUtente;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private LocalDate dataDiNascita;
    private String matricola;
    private String profilePictureLink;


    public User(String nomeUtente, String nome, String cognome, String email, String password, LocalDate dataDiNascita, String matricola, String profilePictureLink) {
        this.nomeUtente = nomeUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.dataDiNascita = dataDiNascita;
        this.matricola = matricola;
        this.profilePictureLink = profilePictureLink;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDataDiNascita() {
        return dataDiNascita;
    }

    public void setDataDiNascita(LocalDate dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getProfilePictureLink() {
        return profilePictureLink;
    }


}