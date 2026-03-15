package com.project.moviefilterbe.util;

import com.project.moviefilterbe.web.dto.omdb.OmdbSearchResponseDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbCreditsDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbDetailResponseDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbReleaseDatesDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbWatchProviderDto;

import java.util.*;
import java.util.stream.Collectors;

public final class TmdbApiUtil {

    private TmdbApiUtil() {
        throw new AssertionError("Utility class");
    }

    public static String getExtractGenre(List<TmdbDetailResponseDto.Genres> genres) {
        return genres.stream()
                .map(c -> c.getName())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    public static String getExtractOtt(TmdbWatchProviderDto.Results provider) {
        return Optional.ofNullable(provider.getKr())
                .map(TmdbWatchProviderDto.CountryData::getFlatrate) // flatrate 리스트 추출
                .map(flatrate -> flatrate.stream()
                        .map(p -> {
                            String platform = p.getProviderName();
                            if (platform.contains("Disney")) return "DISNEY";
                            if (platform.contains("Netflix")) return "NETFLIX";
                            if (platform.contains("wavve")) return "WAVVE";
                            if (platform.contains("Watcha")) return "WATCHA";
                            if (platform.contains("Amazon")) return "AMAZON";
                            if (platform.contains("Coupang")) return "COUPANG";
                            return platform.toUpperCase();
                        })
                        .distinct()
                        .collect(Collectors.joining(", ")))
                .orElse("NONE");
    }

    public static String getExtractCast(List<TmdbCreditsDto.Cast> cast) {
        return cast.stream()
                .filter(c -> c.getOrder() <= 4)
                .map(c -> c.getName())
                .collect(Collectors.joining(", "));
    }

    public static String getExtractCrew(List<TmdbCreditsDto.Crew> crew) {
        return crew.stream()
                .filter(c -> List.of("Director", "Executive Producer", "Screenplay").contains(c.getJob()))
                .map(c -> c.getName())
                .distinct()
                .limit(5)
                .collect(Collectors.joining(", "));
    }

    public static String getExtractRating(List<TmdbReleaseDatesDto.Results> items) {
        return items.stream()
                .filter(result -> "KR".equalsIgnoreCase(result.getIsoId()))
                .findFirst()
                .map(krResult -> krResult.getDetail().stream()
                        .filter(item -> item.getRating() != null && !item.getRating().isEmpty())
                        .sorted(Comparator.comparingInt(i -> {
                            int type = i.getType();
                            if (type == 3) return 1;
                            if (type == 4) return 2;
                            if (type == 6) return 3;
                            return 4;
                        }))
                        .map(TmdbReleaseDatesDto.ReleaseDates::getRating)
                        .findFirst()
                        .orElse("미정"))
                .orElse("NONE");
    }

    public static Map<String, String> getExtractScore(OmdbSearchResponseDto omdbSearchResponseDto) {
        String imdbScore = omdbSearchResponseDto.getRatings().stream()
                .filter(r -> "Internet Movie Database".equals(r.getSource()))
                .findFirst()
                .map(OmdbSearchResponseDto.Rating::getValue)
                .filter(val -> !val.equalsIgnoreCase("N/A"))
                .orElse("0/10");
        String metaScore = omdbSearchResponseDto.getRatings().stream()
                .filter(r -> "Metacritic".equals(r.getSource()))
                .findFirst()
                .map(OmdbSearchResponseDto.Rating::getValue)
                .filter(val -> !val.equalsIgnoreCase("N/A"))
                .orElse("0/100");
        String tomatoScore = omdbSearchResponseDto.getRatings().stream()
                .filter(r -> "Rotten Tomatoes".equals(r.getSource()))
                .findFirst()
                .map(OmdbSearchResponseDto.Rating::getValue)
                .filter(val -> !val.equalsIgnoreCase("N/A"))
                .orElse("0%");

        Map<String, String> result = new HashMap<>();
        result.put("imdb", imdbScore);
        result.put("meta", metaScore);
        result.put("tomato", tomatoScore);

        return result;
    }
}
