package com.dku.blindnavigation.navigation.dto;

import java.util.List;

public class DestinationResponse {
    private SearchPoiInfo searchPoiInfo;

    public DestinationResponse() {
    }

    public DestinationResponse(SearchPoiInfo searchPoiInfo) {
        this.searchPoiInfo = searchPoiInfo;
    }

    public SearchPoiInfo getSearchPoiInfo() {
        return searchPoiInfo;
    }

    public List<Poi> getPois() {
        return getSearchPoiInfo().getPois().getPoi();
    }

    public static class SearchPoiInfo {
        private Pois pois;

        public SearchPoiInfo() {
        }

        public SearchPoiInfo(Pois pois) {
            this.pois = pois;
        }

        public Pois getPois() {
            return pois;
        }


    }

    public static class Pois {
        private List<Poi> poi;

        public Pois() {
        }

        public Pois(List<Poi> poi) {
            this.poi = poi;
        }

        public List<Poi> getPoi() {
            return poi;
        }
    }
}
