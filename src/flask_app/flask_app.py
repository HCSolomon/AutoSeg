from flask import Flask, Markup, render_template, request
import random
import sys
sys.path.append('/home/ubuntu/Watson')
from src.postgresql.postgres_helpers import get_counts, get_latest

app = Flask(__name__)

colors = [
    "#F7464A", "#46BFBD", "#FDB45C", "#FEDCBA",
    "#ABCDEF", "#DDDDDD", "#ABCABC", "#4169E1",
    "#C71585", "#FF4500", "#FEDCBA", "#46BFBD"]

@app.route('/')
def homepage():
    models = get_latest()
    model_labels = []
    model_accs = []
    for model in models:
        model_labels.append(model[0])
        model_accs.append(model[4] * 100)
    return render_template('main.html', title='Accuracies of Latest Trained Models', 
                            max=100, 
                            values=model_accs, 
                            labels=model_labels)

@app.route('/', methods=['POST'])
def stats():
    if request.method == 'POST':
        model = request.form['model-list']
        images = request.form['imageset-list']
        class_counts = get_counts(model, images)
        pie_labels = []
        pie_values = []
        colors = []
        for label in class_counts:
            pie_labels.append(label[0])
            pie_values.append(label[1])
        models = get_latest()
        model_labels = []
        model_accs = []
        for model in models:
            model_labels.append(model[0])
            model_accs.append(model[4] * 100)
        for i in range(20):
            r = lambda: random.randint(0,255)
            colors.append('#%02X%02X%02X' % (r(),r(),r()))
        return render_template('main.html', title='Class Composition of ' + model_name, 
                                piemax=17000, 
                                barmax=100, 
                                set=zip(pie_values, pie_labels, colors), 
                                values=model_accs, 
                                labels=model_labels)
    else:
        return "No information sent" 

if __name__ == "__main__":
    app.run(host='0.0.0.0')