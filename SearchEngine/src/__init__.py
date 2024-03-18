import nltk

def download_nltk_resources():
    resources = ["punkt", "wordnet", "averaged_perceptron_tagger", "stopwords", "universal_tagset", "brown"]  # Add other NLTK resources as needed
    for resource in resources:
        try:
            nltk.data.find(resource)
        except LookupError:
            nltk.download(resource)

download_nltk_resources()