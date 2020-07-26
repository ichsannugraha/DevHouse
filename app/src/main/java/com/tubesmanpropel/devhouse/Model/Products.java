package com.tubesmanpropel.devhouse.Model;

public class Products {

    private String nama, harga, deskripsi, luasTanah, alamat, fasilitas, image, pid, date, time;

    public Products() {
        //Empty Constructor
    }

    public Products(String nama, String harga, String deskripsi, String luasTanah, String alamat, String fasilitas, String image, String pid, String date, String time) {
        this.nama = nama;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.luasTanah = luasTanah;
        this.alamat = alamat;
        this.fasilitas = fasilitas;
        this.image = image;
        this.pid = pid;
        this.date = date;
        this.time = time;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLuasTanah() {
        return luasTanah;
    }

    public void setLuasTanah(String luasTanah) {
        this.luasTanah = luasTanah;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
