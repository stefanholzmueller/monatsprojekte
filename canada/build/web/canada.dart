import 'dart:html';
import 'dart:math';
import 'dart:typed_data';
import 'dart:web_audio';

CanvasElement canvas = querySelector("#canvas");
CanvasRenderingContext2D canvasCtx = canvas.context2D;
ImageElement canadaImg = new ImageElement(src: "canada.png");
ImageElement headImg = new ImageElement(src: "head.png");

AudioContext audioCtx = new AudioContext();
AnalyserNode analyser = audioCtx.createAnalyser();

const int headOffsetX = 240;
const int headOffsetY = 45;
const int maxDegrees = 30;

void main() {
	analyser.fftSize = 2048;

	canadaImg.onLoad.listen((_) {
		headImg.onLoad.listen((_) {
			window.navigator.getUserMedia(audio: true, video: false).then((stream) {
				MediaStreamAudioSourceNode source = audioCtx.createMediaStreamSource(stream);
				source.connectNode(analyser);

				window.animationFrame.then(update);
			});
		});
	});
}

void update(num n) {
	var dataArray = new Uint8List(analyser.frequencyBinCount);
	analyser.getByteTimeDomainData(dataArray);
	var maximum = dataArray.reduce(max);
	var degrees = (maximum / 128 - 1) * maxDegrees;

	canvas.width = canadaImg.width;
	canvas.height = canadaImg.height;

	canvasCtx.drawImage(canadaImg, 0, 0);
	canvasCtx.translate(headOffsetX + headImg.width, headOffsetY + headImg.height);
	canvasCtx.rotate(degrees * PI / 180);
	canvasCtx.drawImage(headImg, -headImg.width, -headImg.height);
	canvasCtx.restore();

	window.animationFrame.then(update);
}
