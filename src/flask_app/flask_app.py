from flask import Flask, Markup, render_template, request
import random
import sys
sys.path.append('./../..')
from src.postgresql.postgres_helpers import get_counts, get_latest, get_models_and_labels

app = Flask(__name__)

@app.route('/')
def homepage():
    models = get_latest()
    model_labels = []
    model_accs = []
    model_names, imageset_names = get_models_and_labels()
    for mdl in models:
        model_labels.append(mdl[0])
        model_accs.append(mdl[4] * 100)
    pie_labels = []
    pie_values = []
    colors = []
    for i in range(20):
        r = lambda: random.randint(0,255)
        colors.append('#%02X%02X%02X' % (r(),r(),r()))
    return render_template('main.html', title='Accuracies of Latest Trained Models', 
                            piemax=17000, 
                            barmax=100, 
                            set=zip(pie_values, pie_labels, colors), 
                            values=model_accs, 
                            labels=model_labels,
                            imagesets=imageset_names)

@app.route('/', methods=['POST'])
def stats():
    if request.method == 'POST':
        model = request.form['model-list']
        images = request.form['imageset-list']
        class_counts, confidence_scores = get_counts(model, images)
        pie_labels = []
        pie_values = []
        colors = []
        for label in class_counts:
            pie_labels.append(label[0])
            pie_values.append(label[1])

        confidence_labels = []
        confidence_values = []
        for label in confidence_scores:
            confidence_labels.append(label[0])
            confidence_values.append(float(label[1]) * 100)
            
        models = get_latest()
        model_labels = []
        model_accs = []
        for mdl in models:
            model_labels.append(mdl[0])
            model_accs.append(mdl[4] * 100)

        for i in range(20):
            r = lambda: random.randint(0,255)
            colors.append('#%02X%02X%02X' % (r(),r(),r()))

        model_names, imageset_names = get_models_and_labels()

        return render_template('main.html', title=images, 
                                piemax=17000, 
                                barmax=100, 
                                set=zip(pie_values, pie_labels, colors), 
                                values=model_accs, 
                                labels=model_labels,
                                imagesets=imageset_names,
                                conf_labels=confidence_labels,
                                conf_values=confidence_values)
    else:
        return("Temporarily Down")

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)