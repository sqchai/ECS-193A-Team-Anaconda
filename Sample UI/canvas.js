/*------------------------- Setup & Resizing Canvas -------------------------*/

const canvas = document.querySelector("#canvas");
const ctx = canvas.getContext("2d");
var video = document.querySelector("#video");
canvas.height = window.innerHeight;
canvas.width = window.innerWidth;

/*------------------------------ Variables ----------------------------------*/

let painting = false;		// for mousemove event
var vertices = [];

var isBezier = true; 		// for switching Bezier & B-spline curves
var bezierVertices = [];	// hold bezier vertices along the curve
var knots = [];				// hold knots for b-spline
var bSplineVertices = [];	// hold b-spline vertices along the curve
let orderK = 3;
let totalBezierVertices = 1000;
let totalBsplineVertices = 1000;

var sysInfoCount = 0;

/*------------------ Helper Funct For Implementing Struct -------------------*/

function GetFactorial(n){
	var result = 1;
	for (var i = 2; i <= n; i++) {
		result = result * i;
	}
	return result;
}

function GetNCR(n, r){
	return GetFactorial(n) / (GetFactorial(r) * GetFactorial(n - r));
}

function DeBoor(k, i, x) {
	if (k == 0) {
		if (knots[i] <= x && x < knots[i+1]) {
			return 1.0;
		} else {
			return 0.0;
		}
	}
	return ((x - knots[i]) / (knots[i+k] - knots[i]) * DeBoor(k-1, i, x)) + ((knots[i+k+1] - x) / (knots[i+k+1] - knots[i+1]) * DeBoor(k-1, i+1, x));
}

function GetBezier(){
	bezierVertices = [];
	var totalVertices = vertices.length;
	for (var i = 0; i < totalBezierVertices; i++) {
		var bezierX = 0; var bezierY = 0;
		var t = i * 1.0 / totalBezierVertices;
		var n = totalVertices - 1;
		for (var j = 0; j < totalVertices; j++) {
			bezierX += vertices[j].x * GetNCR(n, j) * Math.pow(t, j) * Math.pow(1-t, n-j);
			bezierY += vertices[j].y * GetNCR(n, j) * Math.pow(t, j) * Math.pow(1-t, n-j);
		}
		bezierVertices.push({x: bezierX, y: bezierY,});
	}

}

function GetBspline(){
	bSplineVertices = [];
	knots = [];
	for (var i = 0; i < orderK + vertices.length; i++) {
		knots.push(i);
	}
	var k = orderK - 1;
	for (var i = 0; i < totalBsplineVertices; i++) {
		var x = (i * 1.0 / totalBsplineVertices) * (vertices.length - k) + k;
		var bsplineX = 0; var bsplineY = 0;
		for (var j = 0; j < vertices.length; j++) {
			bsplineX += vertices[j].x * DeBoor(k, j, x);
			bsplineY += vertices[j].y * DeBoor(k, j, x);
		}
		bSplineVertices.push({x: bsplineX, y: bsplineY});
	}
}

/*------------------------------ Functions ----------------------------------*/

function down(e){
	painting = true;
	vertices.push({
		x: e.clientX, 
		y: e.clientY,
	})
	Draw();
}

function up(){
	painting = false;
}

function DrawLine(point1x, point1y, point2x, point2y) {
	ctx.beginPath();
	ctx.moveTo(point1x, point1y);
	ctx.lineTo(point2x, point2y);
	ctx.stroke();
}

function PrintVertex(x, y, idx) {
	ctx.font = "30px Arial";
	ctx.fillText("vertex"+idx+" (x,y) ("+x+","+y+") ", 10, 50 * (idx+1));
}

function PrintSysInfo(text) {
	ctx.font = "30px Arial";
	ctx.fillText(text + " " + bezierVertices.length + " " + totalBezierVertices, 1000, 50 * (sysInfoCount + 1));
}

function Draw(){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	if (!painting || vertices.length == 0) return;
	ctx.lineWidth = 10;
	ctx.lineCap = "round";

	// draw straight lines of user input 
	if (vertices.length == 1) {
		DrawLine(vertices[0].x, vertices[0].y, vertices[0].x, vertices[0].y);
		PrintVertex(vertices[0].x, vertices[0].y, 0);
	} else {
		PrintVertex(vertices[0].x, vertices[0].y, 0);
		for (var i = 1; i < vertices.length; i++) {
			DrawLine(vertices[i-1].x, vertices[i-1].y, vertices[i].x, vertices[i].y);
			PrintVertex(vertices[i].x, vertices[i].y, i);
		}
	}

	ctx.lineWidth = 5;
	ctx.lineCap = "round";
	// draw Bezier if isBezier
	if (isBezier) {
		if (vertices.length >= 3) {
			GetBezier();
			for (var i = 1; i < bezierVertices.length; i++) {
				DrawLine(bezierVertices[i-1].x, bezierVertices[i-1].y, bezierVertices[i].x, bezierVertices[i].y);
			}
		}
	// draw Bspline if !isBezier
	} else {
		if (vertices.length >= 3) {
			GetBspline();
			for (var i = 1; i < bSplineVertices.length; i++) {
				DrawLine(bSplineVertices[i-1].x, bSplineVertices[i-1].y, bSplineVertices[i].x, bSplineVertices[i].y);
			}
		}
	}
}

/*--------------------------- Video Background -------------------------*/
navigator.getUserMedia = navigator.getUserMedia ||
                         navigator.webkitGetUserMedia ||
                         navigator.mozGetUserMedia;

if (navigator.getUserMedia) {
   navigator.getUserMedia({ audio: false, video: { width: 1280, height: 720 } },
      function(stream) {
         video.srcObject = stream;
         video.onloadedmetadata = function(e) {
           video.play();
         };
      },
      function(err) {
         console.log("The following error occurred: " + err.name);
      }
   );
} else {
   console.log("getUserMedia not supported");
}

/*--------------------------- Canvas EventListeners -------------------------*/

canvas.addEventListener('mousedown', down);
canvas.addEventListener('mouseup', up);

/*------------------------------- Buttons -----------------------------------*/

const saveButton 	= document.getElementById("save-btn");
const undoButton 	= document.getElementById("undo-btn");
const clearButton 	= document.getElementById("clear-btn");
const switchButton 	= document.getElementById("switch-btn");

/*--------------------------- Button EventListeners -------------------------*/

saveButton.addEventListener('click', () => {
	alert("Save Button Clicked!");
});

undoButton.addEventListener('click', () => {
	if (vertices.length != 0) {
		vertices.pop();
	}
	painting = true;
	Draw();
});

clearButton.addEventListener('click', () => {
	vertices = [];
	painting = true;
	Draw();
});

switchButton.addEventListener('click', () => {
	isBezier = !isBezier;
})