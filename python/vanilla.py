from collections import Counter
from typing import Dict, List

TEST_SENTENCES = [
    "Are you Bilbo Baggins ?",
    "Hello good sir, what a good day to live in the Shire t'is!",
    "I have three hens in my pen.",
    "Are you aware of these facts?",
    "One does not simply walk into mordor",
    "Fly , you fools .",
    "Fly you fools!",
    "I guide other to a treasure i cannot posess",
    "A god walks.",
    "Imagine Dragons fly above us.",
    "HAVE YOU GOT THE BIG BRAIN?",
    "Oh say, can you see?",
    "Les francais doivent mourir pour leur empereur",
]


class VanillaChatbot:
    def __init__(self, file_name="../lotr.en") -> None:
        self.__utterances: List[List[str]] = self.__read_file(file_name)
        self.__document_ferquencies = self.__find_document_frequencies(self.__utterances)
        self.__tfidfs: List[Dict[str, float]] = []

    def __read_file(self, file_name):
        utterances = []
        with open(file_name, "r") as f:
            for line in f:
                tokens = line.strip().lower().split()
                utterances.append(tokens)
        return utterances

    def __find_document_frequencies(self, utterances):
        return {doc_id: Counter(utterances[doc_id]) for doc_id in range(len(utterances))}
        """
        all_terms = [term for utterance in utterances for term in utterance]
        c = Counter(all_terms)
        print(c.most_common(4))
        return Counter(all_terms)
        """


if __name__ == "__main__":
    chatbot = VanillaChatbot()
