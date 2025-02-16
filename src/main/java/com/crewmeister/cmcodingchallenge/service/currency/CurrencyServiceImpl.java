package com.crewmeister.cmcodingchallenge.service.currency;

import com.crewmeister.cmcodingchallenge.dto.CurrencyDTO;
import com.crewmeister.cmcodingchallenge.util.DocumentUtil;
import com.crewmeister.cmcodingchallenge.util.WebServiceConstant;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);
    private final MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();

    @Override
    public List<CurrencyDTO> getAllCurrencies() {
        try {
            Document xmlDocument = DocumentUtil.getDocumentFromRestAsXml(WebServiceConstant.CURRENCY_URL);
            return xmlDocument != null ? parseCurrencies(xmlDocument) : new ArrayList<>();
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            handleException(ex, "CannotParseXml");
        } catch (HttpClientErrorException ex) {
            handleException(ex, "CannotGetDataFromService");
        }
        return new ArrayList<>();
    }

    private List<CurrencyDTO> parseCurrencies(Document xmlDocument) {
        List<CurrencyDTO> currencyDTOList = new ArrayList<>();
        NodeList currencyNodes = xmlDocument.getElementsByTagName(WebServiceConstant.CURRENCY_TAG);

        for (int i = 0; i < currencyNodes.getLength(); i++) {
            Node currencyNode = currencyNodes.item(i);
            String currencyCode = extractCurrencyCode(currencyNode);
            if (currencyCode.length() == 3) {
                String currencyCommonName = extractCurrencyName(currencyNode);
                currencyDTOList.add(new CurrencyDTO(currencyCode, currencyCommonName));
            }
        }
        return currencyDTOList;
    }

    private String extractCurrencyCode(Node currencyNode) {
        return currencyNode.getAttributes().getNamedItem(WebServiceConstant.CURRENCY_TAG_NAME).getNodeValue();
    }

    private String extractCurrencyName(Node currencyNode) {
        NodeList langNodes = currencyNode.getChildNodes();
        for (int j = 0; j < langNodes.getLength(); j++) {
            Node langNode = langNodes.item(j);
            if (langNode.hasAttributes() && WebServiceConstant.CURRENCY_LANG_NAME.equalsIgnoreCase(
                    langNode.getAttributes().getNamedItem(WebServiceConstant.CURRENCY_LANG_TAG).getNodeValue())) {
                return langNode.getTextContent();
            }
        }
        return "";
    }

    private void handleException(Exception ex, String messageKey) {
        logger.error(ex.getMessage());
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, messageSource.getMessage(messageKey, null, locale));
    }
}

