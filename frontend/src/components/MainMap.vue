<template>
    <div
        ref="map"
        class="main-map"
    >
        <div
            v-show="isShowOverlay"
            ref="overlay"
            class="overlay-tooltip"
        >
            <div class="overlay-content">
                {{ selectedOverlayText }}
                <BFormRating
                    v-model="selectedOverlayRating"
                    class="rating"
                    readonly
                />
            </div>
        </div>
        <ProgressSpinner v-if="processingCount > 0" />
    </div>
</template>

<script>
import axios from 'axios';
import Geocoder from 'ol-geocoder';
import OlLayerTile from 'ol/layer/Tile.js';
import OlVectorSource from 'ol/source/Vector.js';
import OlVectorLayer from 'ol/layer/Vector.js';
import OlView from 'ol/View.js';
import OlMap from 'ol/Map.js';
import OlFeature from 'ol/Feature.js';
import OlPoint from 'ol/geom/Point.js';
import OSM from 'ol/source/OSM.js';
import OlStyle from 'ol/style/Style.js';
import OlIcon from 'ol/style/Icon.js';
import Overlay from 'ol/Overlay.js';
import ProgressSpinner from '@/components/ProgressSpinner.vue';
import { toLonLat, transform } from 'ol/proj.js';
import { defaults } from 'ol/control.js';
import { mapState } from 'vuex';

const EPSG_3857 = 'EPSG:3857';
const EPSG_4326 = 'EPSG:4326';

export default {
    name: 'MainMap',
    components: {
        ProgressSpinner,
    },
    data() {
        return {
            olMap: undefined,
            overlay: undefined,
            isShowOverlay: false,
            selectedOverlayText: undefined,
            selectedOverlayRating: undefined,
            address: undefined,
            vectorSource: undefined,
            iconsSource: undefined,
            processingCount: 0,
        };
    },
    computed: {
        ...mapState({
            reviewsForMap: (state) => state.reviewsForMap,
            curReview: (state) => state.curReview,
            curLon: (state) => state.curLon,
            curLat: (state) => state.curLat,
        }),
    },
    watch: {
        async reviewsForMap() {
            if (this.vectorSource) this.vectorSource.clear();
            this.drawFeatures();
        },
        curReview() {
            if (!this.curReview || !this.curLon || !this.curLat) return;
            const coordinate = [this.curLon, this.curLat];
            this.olMap.getView().setZoom(18);
            this.olMap.getView().setCenter(this.coordi4326To3857(coordinate));
        },
    },
    async mounted() {
        const that = this;
        this.vectorSource = new OlVectorSource(EPSG_3857);
        const vectorLayer = new OlVectorLayer({
            source: this.vectorSource,
        });

        this.olMap = new OlMap({
            target: this.$refs.map,
            controls: defaults({
                attribution: false,
                zoom: false,
                rotate: false,
            }),
            layers: [
                new OlLayerTile({
                    source: new OSM(),
                }),
                vectorLayer,
            ],
            view: new OlView({
                center: this.coordi4326To3857([127.1388684, 37.4449168]), // 경기도 성남
                zoom: 10,
                projection: EPSG_3857,
            }),
        });

        await this.$store.dispatch('setReviewsForMap', this);

        this.drawFeatures();

        this.olMap.on('pointermove', (e) => {
            that.olMap.getTargetElement().style.cursor = '';
            that.isShowOverlay = false;
            that.olMap.removeOverlay(that.overlay);

            that.olMap.forEachFeatureAtPixel(e.pixel, (feature) => {
                if (feature.get('title') !== undefined) {
                    that.isShowOverlay = true;
                    that.selectedOverlayText = feature.get('title');
                    that.selectedOverlayRating = feature.get('grade');

                    const overlay = that.$refs.overlay;

                    that.overlay = new Overlay({
                        element: overlay,
                        position: feature.getGeometry().getCoordinates(),
                        positioning: 'bottom-center',
                        offset: [0, -10],
                    });
                    that.olMap.addOverlay(that.overlay);
                    that.olMap.getTargetElement().style.cursor = 'pointer';
                }
            });
        });

        this.olMap.on('click', async (e) => {
            this.vectorSource.clear();
            geocoder.getSource().clear();
            const [lon, lat] = toLonLat(e.coordinate);
            const addressInfo = await that.getAddress(lon, lat);

            this.$store.commit('setReview', undefined);
            this.$store.commit('setIsDisabledInput', false);
            this.$store.commit('setCurAddress', that.getUiAddress(addressInfo.data.display_name));
            that.$store.commit('setLonLat', { lon, lat });
            this.$store.commit('setIsVisibleReviewList', false);
            await this.$store.commit('setCurFileList', []);

            const point = that.coordi4326To3857([lon, lat]);
            const feature = new OlFeature({
                geometry: new OlPoint(point),
            });
            feature.setStyle(
                new OlStyle({
                    image: new OlIcon({
                        scale: 0.7,
                        src: '//cdn.rawgit.com/jonataswalker/map-utils/master/images/marker.png',
                    }),
                })
            );

            const existFeature = that.olMap.forEachFeatureAtPixel(e.pixel, (feature) => {
                this.$store.commit('setCurReviewId', feature.get('reviewId'));
                this.$store.commit('setIsDisabledInput', true);
                this.$store.dispatch('setReview', this);
                return true;
            });

            if (!existFeature) this.vectorSource.addFeature(feature);
        });

        const geocoder = new Geocoder('nominatim', {
            provider: 'osm',
            lang: 'kr',
            placeholder: '주소 검색',
            limit: 5, // 자동 완성 결과 최대 개수
            autoComplete: true,
            keepOpen: true,
        });
        this.olMap.addControl(geocoder);

        geocoder.on('addresschosen', (evt) => {
            this.vectorSource.clear();
            that.$store.commit('setCurAddress', that.getUiAddress(evt.address.details.name));
            that.$store.commit('setLonLat', {
                lon: evt.place.lon,
                lat: evt.place.lat,
            });
        });
    },
    methods: {
        drawFeatures() {
            if (this.iconsSource) this.iconsSource.clear();

            this.iconsSource = new OlVectorSource(EPSG_3857);
            const iconsLayer = new OlVectorLayer({
                source: this.iconsSource,
            });
            const style = new OlStyle({
                image: new OlIcon({
                    scale: 0.8,
                    src: require('../assets/images/spot.png'),
                }),
            });
            const features = this.reviewsForMap.map((review) => {
                const point = this.coordi4326To3857([review.lon, review.lat]);
                const feature = new OlFeature({
                    geometry: new OlPoint(point),
                });
                feature.set('title', review.title);
                feature.set('grade', review.grade);
                feature.set('reviewId', review.id);
                feature.setStyle(style);

                return feature;
            });
            this.iconsSource.addFeatures(features);

            this.olMap.addLayer(iconsLayer);
        },
        coordi4326To3857(coord) {
            return transform(coord, EPSG_4326, EPSG_3857);
        },
        getUiAddress(str) {
            return str.split(', ').reverse().join(' ');
        },
        getAddress(lon, lat) {
            return axios.get('https://nominatim.openstreetmap.org/reverse', {
                params: {
                    format: 'json',
                    lon: lon,
                    lat: lat,
                },
            });
        },
    },
};
</script>

<style lang="scss" scoped>
.main-map {
    width: 100%;
    height: 100%;

    .overlay-tooltip {
        border-radius: 5px;
        background-color: rgba(0, 0, 0, 0.5);
        padding: 5px 10px;
        color: white;
        text-align: center;

        > .overlay-content::after {
            content: '';
            position: absolute;

            top: 100%;
            left: 50%;
            margin-left: -5px;

            border-width: 5px;
            border-style: solid;
            border-color: rgba(0, 0, 0, 0.5) transparent transparent transparent;
        }

        ::v-deep .rating {
            font-size: 15px;
            background-color: transparent;
            border: none;
            padding: 0;
            margin: 0;
            color: #ffdd00;
            height: unset;
        }
    }

    ::v-deep .ol-geocoder {
        position: absolute;
        right: 0;
        padding: 10px;

        button {
            display: none;
        }

        input::placeholder {
            color: white;
            opacity: 0.7;
        }

        input,
        ul {
            border-style: none;
            width: 200px;
            background-color: rgba(0, 0, 0, 0.5);
            border-radius: 5px;
            border-color: unset;
            padding: 0 5px;
            color: white;
        }

        ul {
            margin-top: 5px;
            padding: 0;
            list-style: none;

            li:hover {
                background-color: rgba(0, 0, 0, 0.3);
            }

            li {
                padding: 5px 10px;
                font-size: 13px;

                a {
                    text-decoration: none;

                    .gcd-road {
                        color: white;
                    }
                }
            }
        }
    }
}
</style>
