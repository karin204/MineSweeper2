package com.example.karin.minesweeper.logic;

/**
 * Created by Avi on 07/01/2017.
 */

public class PlayerScore implements Comparable
{
    private String playerName;
    private String playerTime;
    private String playerLevel;
    private Double playerAltitude;
    private Double playerLongitude;

    public PlayerScore(){}
    public PlayerScore(String playerName, String playerTime, String playerLevel, Double playerAltitude, Double playerLongitude) {
        this.playerName = playerName;
        this.playerTime = playerTime;
        this.playerLevel = playerLevel;
        this.playerAltitude = playerAltitude;
        this.playerLongitude = playerLongitude;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerTime() {
        return playerTime;
    }

    public void setPlayerTime(String playerTime) {
        this.playerTime = playerTime;
    }

    public String getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(String playerLevel) {
        this.playerLevel = playerLevel;
    }

    public Double getPlayerAltitude() {
        return playerAltitude;
    }

    public void setPlayerAltitude(Double playerAltitude) {
        this.playerAltitude = playerAltitude;
    }

    public Double getPlayerLongitude() {
        return playerLongitude;
    }

    public void setPlayerLongitude(Double playerLongitude) {
        this.playerLongitude = playerLongitude;
    }

    @Override
    public int compareTo(Object o) {
        PlayerScore p = (PlayerScore) o;

        for(int i = 0; i < this.playerTime.length(); i++)
        {
            if (this.playerTime.charAt(i) < p.getPlayerTime().charAt(i))
                return -1;
            else if (this.playerTime.charAt(i) > p.getPlayerTime().charAt(i))
                return 1;
        }
        return 0;
    }
}
