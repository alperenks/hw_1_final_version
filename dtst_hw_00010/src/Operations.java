import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Operations {
	private HashTable<String, MediaData> database;
    long difference;
    static double min = 999999999;
    static double max = 0;

    static double averageTime;
    
	public Operations(double loadFactor, boolean isSSF, boolean isDoubleHash) {
			database = new HashTable<>(loadFactor, isSSF, isDoubleHash);
	}

	// Operations sınıfına eklenmesi gereken getter metodu
	public HashTable<String, MediaData> getDatabase() {
		return this.database;
	}

	/**
	 * Dataseti yükler ve hash tabloya kaydeder.
	 * 
	 * @param filePath Dataset dosyasının yolu
	 */
	public void loadDataset(String filePath) {
        long time = System.nanoTime();

		try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Alp\\Desktop\\hw_1_final_version\\dtst_hw_00010\\movies_dataset.csv"))) {

			String header = reader.readLine(); // Başlık satırını atla

			if (header == null || header.split(",").length != 10) {
				System.err.println("Dataset formatı hatalı: Başlık kontrol edin.");
				return;
			}

			String line;
			int lineNumber = 0;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

				if (values.length != 10) {
					System.err.println("Hatalı satır (sütun sayısı uyuşmuyor): " + lineNumber);
					continue;
				}

				try {
					String imdbId = values[5].trim();
					if (imdbId.isEmpty() || !imdbId.startsWith("tt")) {
						continue;
					}

					String title = values[1].trim();
					String type = values[2].trim();
					String genres = values[3].trim();
					String releaseYear = values[4].trim();
					double rating = values[6].isEmpty() ? 0.0 : Double.parseDouble(values[6].trim());
					int votes = values[7].isEmpty() ? 0 : Integer.parseInt(values[7].trim());
					String platform = values[8];

					MediaData media = database.getValue(imdbId);

					if (media == null) {
						media = new MediaData(imdbId, type, title, releaseYear, List.of(genres.split(", ")), rating,
								votes);
						database.add(imdbId, media);
					}

					if (platform != null && !platform.trim().isEmpty()) {
						media.addPlatform(platform.trim());
					}

					if (values[9] != null && !values[9].trim().isEmpty()) {
						List<String> countries = List.of(values[9].trim().split(", "));
						for (String country : countries) {
							media.addCountry(country.trim());
						}
					}

				} catch (NumberFormatException e) {
					System.err.println("Veri dönüşüm hatası at line " + lineNumber + ": " + e.getMessage());
				}
			}
            long off_time = System.nanoTime();
            
            difference = off_time - time;
			System.out.println("Dataset başarıyla yüklendi. Toplam kayıt: " + database.getSize());
		} catch (IOException e) {
			System.err.println("Dosya okunamadı: " + e.getMessage());
		}
	}

	/**
	 * IMDb ID kullanarak bir medya öğesini arar.
	 * 
	 * @param imdbId Aranacak IMDb ID
	 */
	public void searchMedia(String imdbId) {
		MediaData media = database.getValue(imdbId);
		if (media != null) {
			media.printMediaData();
		} else {
			System.out.println("Media bulunamadı: " + imdbId);
		}
	}

	/**
	 * Belirli bir ülkede mevcut olan medyaları listeler.
	 * 
	 * @param countryCode Ülke kodu
	 */
	public void listMediaInCountry(String countryCode) {
		System.out.println(countryCode + " ülkesinde mevcut olan medyalar:");
		database.getKeyIterator().forEachRemaining(key -> {
			MediaData media = database.getValue(key);
			if (media != null && media.getCountries().contains(countryCode)) {
				media.printMediaData();
			}
		});
	}

	/**
	 * Tüm platformlarda mevcut olan medyaları listeler.
	 */
	public void listMediaOnAllPlatforms() {
		System.out.println("Tüm platformlarda mevcut olan medyalar:");
		int mediaChecked = 0;
		int mediaListed = 0;

		Iterator<String> keyIterator = database.getKeyIterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			MediaData media = database.getValue(key);

			if (media != null) {
				mediaChecked++;
				Alist<String> platforms = media.getPlatforms();
				if (platforms == null || platforms.getLength() == 0) {
					System.out.println("Platform bilgisi eksik. Media ID: " + media.getImdbId());
					continue;
				}

				List<String> platformList = new ArrayList<>();
				for (int i = 1; i <= platforms.getLength(); i++) {
					platformList.add(platforms.getEntry(i).trim());
				}

				if (platformList.contains("Netflix") && platformList.contains("Amazon Prime") && platformList.contains("Hulu") &&
						platformList.contains("HBO Max") && platformList.contains("Apple TV+")) {
					mediaListed++;
					media.printMediaData();
				}
			} else {
				System.out.println("Media is null for key: " + key);
			}
		}

		System.out.println("Toplam medya kontrol edildi: " + mediaChecked);
		System.out.println("Listeye eklenen medya: " + mediaListed);
	}

	/**
	 * IMDb puanına göre en iyi 10 medyayı listeler.
	 */
	public void listTop10Media() {
		System.out.println("En iyi 10 medya:");
		List<MediaData> mediaList = new ArrayList<>();

		database.getKeyIterator().forEachRemaining(key -> {
			MediaData media = database.getValue(key);
			if (media != null) {
				mediaList.add(media);
			}
		});

		mediaList.stream().sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating())) // IMDb puanına göre
																								// sıralama  hamza
				.limit(10).forEach(MediaData::printMediaData);
	}

	/**
	 * Performans testi gerçekleştirir.
	 * 
	 * @param // searchFilePath Arama dosyasının yolu
	 */
	
    public void runSearchTest(String searchFile) {
        String[] searchedFile = new String[1000];
        try (BufferedReader br = new BufferedReader(new FileReader(searchFile))) {
            String line;
        	int i = 0;
            while ((line = br.readLine()) != null) {
                searchedFile[i] = line;
                i++;
            }
        }      
         catch (IOException e) {
            System.out.println("File not found.");;
        }

        long timer1 = System.nanoTime();
        for (int i = 0; i < searchedFile.length; i++) {
        	String key = searchedFile[i];
        	long timer = System.nanoTime();
        	if (database.contains(key)) {
        		long timer_off = System.nanoTime();
        		double searchDifference = timer_off - timer;
        		if(searchDifference > max) {
        			max = searchDifference;
        		}
        		else if (searchDifference < min) {
        			min = searchDifference;
        		}
        	}
        }
        long timer1_off = System.nanoTime();
        averageTime = (timer1_off - timer1) / 1000;
        
        System.out.println("Max Search Time: " + max);
        System.out.println("Min Search Time: " + min);
        System.out.println("Average Search Time: " + averageTime);
        System.out.println("Index Time: " + difference);
        System.out.println("Collision Count: " + database.getCollisionCount());
    }
}
