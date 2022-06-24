<template>
    <div class="main-map" ref="map">
    </div>
</template>

<script>
import axios from 'axios'
import OlLayerTile from 'ol/layer/Tile.js'
import OlVectorSource from 'ol/source/Vector.js'
import OlVectorLayer from 'ol/layer/Vector.js'
import OlView from 'ol/View.js'
import OlMap from 'ol/Map.js'
import OlFeature from 'ol/Feature.js';
import OlPoint from 'ol/geom/Point';
import OSM from 'ol/source/OSM'
import Geocoder from 'ol-geocoder'
import {toLonLat, transform} from 'ol/proj.js'
import {defaults} from 'ol/control.js'
import OlStyle from 'ol/style/Style.js'
import OlIcon from 'ol/style/Icon.js'
import { process } from '@/common/Api'

const EPSG_3857 = 'EPSG:3857';
const EPSG_4326 = 'EPSG:4326';

export default {
    name: 'MainMap',
    data() {
        return {
            olMap: undefined,
            address: undefined
        }
    },
    async created () {

    },
    async mounted() {
        const that = this;
        const vectorSource = new OlVectorSource(EPSG_3857);
        const vectorLayer = new OlVectorLayer({
            source: vectorSource
        });

        const iconsSource = new OlVectorSource(EPSG_3857);
        const iconsLayer = new OlVectorLayer({
            source: iconsSource
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
                    source: new OSM()
                }),
                vectorLayer,
                iconsLayer
            ],
            view: new OlView({
                center: this.coordi4326To3857([127.1388684, 37.4449168]), // 경기도 성남
                zoom: 10,
                projection: EPSG_3857
            })
        })

        const reviews = await this.getReviews();
        this.$store.commit('setReviews', reviews);

        const style = new OlStyle({
            image: new OlIcon({
                scale: 0.08,
                src: require('../assets/images/pin.png')
            })
        });
        const features = this.$store.state.reviews.map(review => {
            const point = that.coordi4326To3857([review.lon, review.lat]);
            const feature = new OlFeature({
                geometry: new OlPoint(point)
            });
            feature.setStyle(style);
            return feature;
        })
        iconsSource.addFeatures(features);


        this.olMap.on('click', async (e) => {
            vectorSource.clear();
            geocoder.getSource().clear();
            const [lon, lat] = toLonLat(e.coordinate)
            const addressInfo = await that.getAddress(lon, lat)
            that.setUiAddress(addressInfo.data.display_name);

            const point = that.coordi4326To3857([lon, lat]);
            const feature = new OlFeature({
                geometry: new OlPoint(point)
            })
            feature.setStyle(new OlStyle({
                image: new OlIcon({
                    scale: 0.7,
                    src: '//cdn.rawgit.com/jonataswalker/map-utils/master/images/marker.png'
                })
            }))
            vectorSource.addFeature(feature);
        })

        const geocoder = new Geocoder('nominatim', {
            provider: 'osm',
            lang: 'kr',
            placeholder: '주소 검색',
            limit: 5, // 자동 완성 결과 최대 개수
            autoComplete: true,
            keepOpen: true
        });
        this.olMap.addControl(geocoder);

        geocoder.on('addresschosen', (evt) => {
            vectorSource.clear();
            that.setUiAddress(evt.address.details.name);
        });

    },
    methods: {
        async getReviews() {
            return await process(this, async () => {
                const result = await axios.get('/api/review/getReviews');
                return result.data;
            })
        },
        coordi4326To3857(coord) {
            return transform(coord, EPSG_4326, EPSG_3857);
        },
        setUiAddress(str) {
            this.$root.$refs.sideBar.address = str.split(', ').reverse().join(' ');
        },
        getAddress(lon, lat) {
            return axios.get(
                'https://nominatim.openstreetmap.org/reverse',
                {
                    params: {
                        format: 'json',
                        lon: lon,
                        lat: lat
                    }
                })
        }
    }
}
</script>

<style lang="scss" scoped>
.main-map {
    width: 100%;
    height: 100%;

    ::v-deep.ol-geocoder {
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

        input, ul {
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
