@Service
@RequiredArgsConstructor
public class GeminiApiService {
  private static final RestTemplate restTemplate = new RestTemplate();

    public void geminiSearchMovies(String people, String[] motion, String[] genre) {
        String motions = String.join(",", motion);
        String genres = String.join(",", genre);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", "AIzaSyB5XlSASbNyVn5EgYWUnDsSy4SgYNSetrM");
        // ai prompt 수정 - 20260211 ms
        String promptText = String.format(
            "너는 영화 추천 전문 API 시스템이야. 사용자의 감정, 인원, 장르 데이터를 기반으로 최적의 영화 20개를 추천한다.\n\n" +
            "[규칙]\n" +
            "1. 모든 응답은 반드시 순수한 JSON 형식으로만 출력한다. 다른 설명은 생략한다.\n" +
            "2. 영화 제목은 반드시 '영화제목(개봉연도)' 형식으로, 무조건 한국어로 작성한다.\n" +
            "3. 최근 3년 이내 신작 5개 이상 포함, 대중적 작품과 명작 비율 7:3 엄수.\n\n" +
            "[JSON 구조]\n" +
            "{\n" +
            "  \"recommended_movies\": [\"제목(연도)\", \"제목(연도)\", ...]\n" +
            "}\n\n" +
            "[요청 내용]\n" +
            "감정: %s / 인원: %s명 / 장르: %s",
            motions, people, genres
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", promptText))
                )),
                "generationConfig", Map.of(
                        "response_mime_type", "application/json"
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            JsonNode root = restTemplate.postForObject(uri, entity, JsonNode.class);

            if (root != null) {
                String rawJson = root.path("candidates").get(0)
                                    .path("content").path("parts").get(0)
                                    .path("text").asText();

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<String>> resultMap = objectMapper.readValue(rawJson, new TypeReference<>() {});
                System.out.println(resultMap.get("recommended_movies"));
            }

            // if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
            //     String jsonText = response.getCandidates().get(0).getContent().getParts().get(0).getText();
                
            //     // Jackson ObjectMapper를 사용하여 JSON 파싱
            //     ObjectMapper objectMapper = new ObjectMapper();
            //     Map<String, List<String>> resultMap = objectMapper.readValue(jsonText, new TypeReference<>() {});
                
            //     // return resultMap.get("recommended_movies");
            // }
        } catch (Exception e) {
            System.err.println("Gemini 호출 또는 파싱 에러: " + e.getMessage());
        }
    }
}
