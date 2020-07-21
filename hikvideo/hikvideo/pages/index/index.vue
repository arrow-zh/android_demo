<template>
	<div>
		<button type="primary" @click="startLive">Live</button>
		<button type="primary" @click="startPlayback">playback</button>
	</div>
</template>

<script>
	// 获取 module 
	var hikvideo = uni.requireNativePlugin("hikvideo")
	export default {
		onLoad() {
			
		},
		methods: {
			startLive() {
				hikvideo.startLive({
					 "titlebar-color":"#FF0000", 
					 "device-id": "72d1f36a8b0941b2abaee25376a8c5b5",
					 "device-name": "TS_办公室",
					 "project-name": "成都办公室铁塔项目",
					 "construct-name": "成都施工单位",
					 "unit-type-str": "塔身"})
			},
			startPlayback(){
				hikvideo.startPlayback({
					 "titlebar-color":"#FF0000", 
					 "device-id": "72d1f36a8b0941b2abaee25376a8c5b5",
					 "device-name": "TS_办公室",
					 "project-name": "成都办公室铁塔项目",
					 "construct-name": "成都施工单位",
					 "unit-type-str": "塔身",
					 "bind-time": "2020-06-19 18:12:12",
					 "unbind-time": ""})
			}
		}
	}
</script>ss