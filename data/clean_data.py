import os
import sys
import re
import string
import fileinput
from shutil import copyfile
from os.path import basename

def get_basename(filename, current_path):
	base = os.path.basename(current_path+'/'+filename)
	name = os.path.splitext(base)[0]
	return name

def replacement(filename, find_word, word_replace):
	print("replacing %s with %s\n" %(find_word, word_replace))
	file = open(filename, "r+")
	for line in fileinput.input(filename):
		file.write(line.replace(find_word, word_replace))
	file.close()

def clean_Files(new_filename):
	replacement(new_filename, '"subjects"', '"subject"')
	replacement(new_filename, 'Mixed material', 'Mixed%20material')
	replacement(new_filename, 'Visual material', 'Visual%20material')
	replacement(new_filename, 'Computer file', 'Computer%20file')
	replacement(new_filename, '"linked_data": {', '')
	replacement(new_filename, '}},', '},')
	replacement(new_filename, ', }', '}')
	replacement(new_filename, 'https://open-na.hosted.exlibrisgroup.com/alma/contexts/bib','{"dc": "http://purl.org/dc/elements/1.1/", "bibo": "http://purl.org/ontology/bibo/", "xsd": "http://www.w3.org/2001/XMLSchema#", "dct":"http://purl.org/dc/terms/", "foaf": "http://xmlns.com/foaf/0.1/", "owl": "http://www.w3.org/2002/07/owl#", "rdfs": "http://www.w3.org/2000/01/rdf-schema#", "title": "dct:title", "creator": {"@id":"dct:creator", "@container": "@set"}, "contributor": {"@id":"dct:contributor", "@container":  "@set"}, "alternative": "dct:alternative", "publisher": {"@id":"dct:publisher", "@container": "@set"}, "language": {"@id":"dct:language", "@type":"@id"}, "subject": {"@id":"dct:subject", "@container": "@set"}, "identifier": {"@id": "dc:identifier", "@container": "@set"}, "description": "dct:description", "date": "dct:date", "medium": "dct:medium ", "format": "dct:format", "type": "dct:type", "place_of_publication": "dct:Location", "sameAs": {"@id": "owl:sameAs","@type": "@id","@container": "@set"}, "label": "rdfs:label", "edition": "bibo:edition", "note": "bibo:Note", "series": "bibo:series", "volume": "bibo:volume", "isbn": "bibo:isbn", "isbn10": "bibo:isbn10", "isbn13": "bibo:isbn13",	"oclcnum": "bibo:oclcnum", "issn": "bibo:issn", "issue": "bibo:issue"}')

if __name__ == "__main__":
	current_path = os.getcwd()
	for filename in os.listdir(current_path):
		if filename.endswith('.jsonld'):
			basename = get_basename(filename, current_path)

			old_filename = current_path+'/'+filename
			new_filename = current_path+'/'+basename+'_fixed.jsonld'
			copyfile(old_filename, new_filename)

			clean_Files(new_filename)

