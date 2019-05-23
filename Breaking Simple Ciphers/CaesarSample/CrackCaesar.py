import sys
import os

def Decipher(cipher, dict):
    """
    Does a simple caesar decipher by going through all the offsets and changing the letters by that offset
    Then compares the new string to the word bank and keeps the string and offset with the highest word match
    :param cipher: cipher Text
    :param dict: common word bank
    :return: decipher text and respective offset
    """
    key = -1
    most_word = 0
    decipher = ''
    for offset in range(0,26):
        #print(offset)
        #offset = 10
        new_word = ""
        for letter in cipher:
            if(letter.isalpha()):
                asci = ord(letter) + offset
                if(letter.isupper()):
                    if(asci > ord('Z')):
                        asci -= 26
                    elif(asci < ord('A')):
                        asci += 26
                elif(letter.islower()):
                    if(asci > ord('z')):
                        asci -= 26
                    elif(asci < ord('a')):
                        asci += 26
                new_word += chr(asci)
            else:
                new_word += letter
        #print(new_word)

        #Check words
        list_words = new_word.split(' ')
        total_word = 0
        for index in list_words:
            if(index.lower() in dict):
                total_word += 1
        #print(total_word)
        if(total_word > most_word):
            most_word = total_word
            key = offset
            decipher = new_word

    return decipher, key

def main():
    cipherText = sys.argv[1]
    dict = sys.argv[2]

    exists = os.path.isfile(cipherText)
    exists2 = os.path.isfile(dict)
    if exists and exists2:
        # Store configuration file values
        file1 = open(dict, "r+")
        file2 = open(cipherText, "r+")

        #print("Output of Read function is ")
        text = file1.read()
        file1.close()
        dict_list = text.split('\n')
        #print(dict_list)

        text2 = file2.read()
        file2.close()

        final, offset = Decipher(text2, dict_list)
        print(offset)
        print(final)

    else:
        print("Could not find files")

if __name__ == "__main__":
    main()