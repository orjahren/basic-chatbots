# JBasicChatBot
The most basic form of chatbot possible. Minimal Java implementation from scratch using TFIDF-vector-similarity

# Usage:
```java
Chatbot cb = null;
try {
    cb = new Chatbot("lotr.en");
}catch(FileNotFoundException e) {
    //handle file not found
    e.printStackTrace();
    System.exit(1);
}

Scanner scanner = new Scanner(System.in);
while (scanner.hasNextLine()) {
    String input = scanner.nextLine();
    String response = cb.getResponse(input);
    
    System.out.printf("Bot: %s%n", response);
}
 ```
 ...Or instantiate it as `Chatbot cb = new Chatbot(file);` and use the `Chatbot.getResponse(input)`-method as you would want

# Some examples of use:

Are you Bilbo Baggins ? : Show yourself!

Hello good sir, what a good day to live in the Shire t'is! : Yes, very good.

I have three hens in my pen. : There are many to call.

Are you aware of these facts? : Keep them close.

One does not simply walk into mordor : Its black gates are guarded by more than just orcs.

Fly , you fools . : No!

Fly you fools! : No!

I guide other to a treasure i cannot posess : Long years have passed.

A god walks. : What?

Imagine Dragons fly above us. : You cannot pass!

HAVE YOU GOT THE BIG BRAIN? : Do you remember what i told you?

Oh say, can you see? : And if we fail, what happens when sauron takes back what is his?

Les francais doivent mourir pour leur empereur : I feel it in the water.
