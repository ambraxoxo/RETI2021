import com.google.gson.*;



import java.lang.reflect.Type;


public class User implements Comparable<User>{
    private String username;
    private String password;
    private int punteggio;
    private String lastWordPlayed;
    private int playedMatches;
    private int wonMatches;
    private int[] guessDistribution;
    private int streak;
    private int longestStreak;
    private int winPerc;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.punteggio = 0;
        this.lastWordPlayed = "";
        this.playedMatches = 0;
        this.wonMatches = 0;
        this.guessDistribution = new int[12];
        this.streak = 0;
        this.longestStreak = 0;
        this.winPerc = 0;
    }
    public User(){}

    public String getLastWordPlayed() {
        return lastWordPlayed;
    }

    public void setLastWordPlayed(String lastWordPlayed) {
        this.lastWordPlayed = lastWordPlayed;
    }

    public int getPlayedMatches() {
        return playedMatches;
    }

    public int getWonMatches() {
        return wonMatches;
    }

    public int[] getGuessDistribution() {
        return guessDistribution;
    }

    public int getStreak() { return streak; }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setPunteggio(int punteggio){
        this.punteggio = punteggio;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPunteggio() {
        return punteggio;
    }

   @Override
    public String toString() {
        return "Utente{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", punteggio='" + punteggio + '\'' +
                ", lastWordPlayed='" + lastWordPlayed + '\'' +
                ", playedMatches='" + playedMatches + '\'' +
                ", wonMatches='" + wonMatches + '\'' +
                '}';
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPlayedMatches(int playedMatches) {
        this.playedMatches = playedMatches;
    }

    public void setWonMatches(int wonMatches) {
        this.wonMatches = wonMatches;
    }

    public void setGuessDistribution(int[] guessDistribution) {
        this.guessDistribution = guessDistribution;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public void setWinPerc(int winPerc) {
        this.winPerc = winPerc;
    }

    /**
     * Metodo che usa TreeSet per ordinare gli utenti
     */
    @Override
    public int compareTo(User o) {
        int result = Integer.compare(this.punteggio, o.punteggio);
        if(result == 0)
            return 1; //return 1 se sono uguali per aggiungere anche i punteggi uguali.
        else
            return Integer.compare(this.punteggio, o.punteggio);
    }

    /**
     * Classe per deserializzare gli oggetti utente
     */
    public static class Deserializer implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            User obj = new User();

            obj.setUsername(jsonObject.get("username").getAsString());
            obj.setPassword(jsonObject.get("password").getAsString());
            obj.setPunteggio(jsonObject.get("punteggio").getAsInt());
            obj.setLastWordPlayed(jsonObject.get("lastWordPlayed").getAsString());
            obj.setPlayedMatches(jsonObject.get("playedMatches").getAsInt());
            obj.setWonMatches(jsonObject.get("wonMatches").getAsInt());
            obj.setGuessDistribution(deserializeIntArray(jsonObject.get("guessDistribution")));
            obj.setStreak(jsonObject.get("streak").getAsInt());
            obj.setLongestStreak(jsonObject.get("longestStreak").getAsInt());
            obj.setWinPerc(jsonObject.get("winPerc").getAsInt());

            return obj;

        }

        /**
         * Metodo che deserializza l'array contenente la guess distribution
         */
        private int[] deserializeIntArray(JsonElement jsonElement) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            int[] array = new int[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
                array[i] = jsonArray.get(i).getAsInt();
            }

            return array;
        }
    }

}
