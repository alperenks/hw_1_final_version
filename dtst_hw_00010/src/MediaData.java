import java.util.List;
public class MediaData {
    private String imdbId;              // Unique key
    private String type;                // Movie or TV Series
    private String title;               // Title of the media
    private String releaseYear;         // Release year
    private List<String> genres;        // List of genres
    private double rating;              // IMDb rating
    private int numberOfVotes;          // Number of votes
    private Alist<String> platforms;    // Platforms where available
    private Alist<String> countries;    // Available countries (ISO codes)

    // Constructor
    public MediaData(String imdbId, String type, String title, String releaseYear, List<String> genres,
                     double rating, int numberOfVotes) {
        this.imdbId = imdbId;
        this.type = type;
        this.title = title;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.rating = rating;
        this.numberOfVotes = numberOfVotes;
        this.platforms = new Alist<>();
        this.countries = new Alist<>();
    }

    // Getters
    public String getImdbId() {
        return imdbId;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public List<String> getGenres() {
        return genres;
    }

    public double getRating() {
        return rating;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public Alist<String> getPlatforms() {
        return platforms;
    }

    public Alist<String> getCountries() {
        return countries;
    }

    // Add a platform (alphabetical order)
    public void addPlatform(String platform) {
        if (platform == null || platform.trim().isEmpty()) {
            return; // Boş veya geçersiz platform eklenemez
        }

        if (platforms == null) {
            platforms = new Alist<>(); // Liste başlatılır
        }

        platform = platform.trim(); // Büyük/küçük harf farkını kaldır ve boşlukları temizle
        int len = platforms.getLength();

        for (int i = 1; i <= len; i++) {
            String existingPlatform = platforms.getEntry(i); // Büyük/küçük harf farkını kaldır
            if (existingPlatform.equals(platform)) {
                return; // Zaten mevcut, ekleme
            }
            if (existingPlatform.compareTo(platform) > 0) {
                platforms.add(i, platform);
                return;
            }
        }

        platforms.add(platform); // Liste boşsa veya sona eklenmesi gerekiyorsa
    }

    // Add a country (alphabetical order)
    public void addCountry(String country) {
        int len = countries.getLength();
        for (int i = 1; i <= len; i++) {
            if (countries.getEntry(i).equals(country)) {
                return; // Zaten mevcut, ekleme
            }
            if (countries.getEntry(i).compareTo(country) > 0) {
                countries.add(i, country);
                return;
            }
        }
        countries.add(country); // Liste boşsa veya sona eklenmesi gerekiyorsa
    }

    // Print media data
    public void printMediaData() {
        // Medya bilgilerini yazdır
        System.out.println("Type: " + title);
        System.out.println("Genre: " + String.join(", ", genres));
        System.out.println("Release Year: " + releaseYear);
        System.out.println("IMDb ID: " + imdbId);
        System.out.println("Rating: " + rating);
        System.out.println("Number of Votes: " + numberOfVotes);

        // Platform bilgilerini yazdır
        if (platforms != null && platforms.getLength() > 0) {
            System.out.println("\n" + platforms.getLength() + " platforms found for " + title + ":");
            for (int i = 1; i <= platforms.getLength(); i++) {
                String platform = platforms.getEntry(i);

                // Platformla ilişkili ülkeleri al
                String countriesForPlatform = getCountriesForPlatform(platform);
                if (!countriesForPlatform.isEmpty()) {
                    System.out.println(platform + " - " + countriesForPlatform);
                } else {
                    System.out.println(platform);
                }
            }
        } else {
            System.out.println("No platforms available for this media.");
        }

        // Ülke bilgilerini yazdır
        if (countries != null && countries.getLength() > 0) {
            System.out.println("\nAvailable Countries:");
            StringBuilder countryList = new StringBuilder();
            for (int i = 1; i <= countries.getLength(); i++) {
                countryList.append(countries.getEntry(i));
                if (i < countries.getLength()) {
                    countryList.append(", ");
                }
            }
            System.out.println(" - " + countryList.toString());
        } else {
            System.out.println("No country information available.");
        }
    }

    /**
     * Verilen platform için uygun ülkeleri döndürür.
     * @param platform Platform adı
     * @return Ülke bilgilerini içeren bir string
     */
    private String getCountriesForPlatform(String platform) {
        // Platformla ilişkili ülke bilgilerini döndür
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= countries.getLength(); i++) {
            String country = countries.getEntry(i);
            if (country.toLowerCase().contains(platform.toLowerCase())) { // Basit eşleşme
                result.append(country).append(", ");
            }
        }

        if (result.length() > 0) {
            result.setLength(result.length() - 2); // Sondaki virgülü kaldır
        }

        return result.toString();
    }

}
