const canvas = document.querySelector("#canvas");
const ctx = canvas.getContext("2d");

//Resizing
canvas.height = window.innerHeight;
canvas.width = window.innerWidth;

//Variables
let painting = false;	// for mousemove event
var inputs = [];

//Functions
function down(e){
	painting = true;
	inputs.push({
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

function PrintInfo(x, y, idx) {
	ctx.font = "30px Arial";
	ctx.fillText("input"+idx+" (x,y) ("+x+","+y+")", 10, 50 * (idx+1));
}

function Draw(){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	if (!painting || inputs.length == 0) return;
	ctx.lineWidth = 10;
	ctx.lineCap = "round";

	if (inputs.length == 1) {
		DrawLine(inputs[0].x, inputs[0].y, inputs[0].x, inputs[0].y);
		PrintInfo(inputs[0].x, inputs[0].y, 0);
	} else {
		PrintInfo(inputs[0].x, inputs[0].y, 0);
		for (var i = 1; i < inputs.length; i++) {
			DrawLine(inputs[i-1].x, inputs[i-1].y, inputs[i].x, inputs[i].y);
			PrintInfo(inputs[i].x, inputs[i].y, i);
		}
	}
}

//Canvas EventListeners
canvas.addEventListener('mousedown', down);
canvas.addEventListener('mouseup', up);

//Buttons
const saveButton = document.getElementById("save-btn");
const undoButton = document.getElementById("undo-btn");
const clearButton = document.getElementById("clear-btn");

//Button EventListeners
saveButton.addEventListener('click', () => {
	alert("Save Button Clicked!");
});

undoButton.addEventListener('click', () => {
	if (inputs.length != 0) {
		inputs.pop();
	}
	painting = true;
	Draw();
});

clearButton.addEventListener('click', () => {
	inputs = [];
	painting = true;
	Draw();
});