package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class CreditCardController {

    // TODO: wire in CreditCard repository here (~1 line)
    @Autowired
    CreditCardRepository creditCardRepository;
    @Autowired
    UserRepository userRepository;  //A service class would keep the UserRepository out of the CreditCardController class.



    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // COMPLETED: Create a credit card entity, and then associate that credit card with user with given userId
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length


        Optional<User> userOptional = userRepository.findById(payload.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            CreditCard creditCard = new CreditCard();
            creditCard.setIssuanceBank(payload.getCardIssuanceBank());
            creditCard.setNumber(payload.getCardNumber());
            creditCard.setOwner(user);
            creditCardRepository.save(creditCard);

            return ResponseEntity.ok(creditCard.getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // COMPLETED: return a list of all credit card associated with the given userId, using CreditCardView class
        //       if the user has no credit card, return empty list, never return null

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<CreditCard> creditCards = user.getCreditCards();
            List<CreditCardView> creditCardViews = new ArrayList<>();
            for (CreditCard creditCard : creditCards) {
                CreditCardView creditCardView = new CreditCardView(creditCard.getIssuanceBank(),creditCard.getNumber());
                creditCardViews.add(creditCardView);
            }
            return ResponseEntity.ok(creditCardViews);
        } else {
            return ResponseEntity.notFound().build();
        }

    }


    //Incomplete... and needs testing...
    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // Completed: Given a credit card number, efficiently find whether there is a user associated with the credit card
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request
        //      Incomplete and needs further testing...
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByNumber(creditCardNumber);
        System.out.print((optionalCreditCard.get().getNumber()));
        if (optionalCreditCard.isPresent()) {
            CreditCard creditCard = optionalCreditCard.get();
            User user = creditCard.getOwner();
            return ResponseEntity.ok(user.getId());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }




    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<Void> updateCreditCardBalance(@RequestBody UpdateBalancePayload[] payload) {

        //TODO: Given a list of transactions, update credit cards' balance history.
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a transaction of {date: 4/10, amount: 10}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 110}]
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //        is not associated with a card.
        //      Incomplete and needs further testing...



        Map<String, CreditCard> creditCardsByNumber = new HashMap<>();

        //retrieve credit card entities.
        for (UpdateBalancePayload updateBalancePayload : payload) {
            Optional<CreditCard> optionalCreditCard = creditCardRepository.findByNumber(updateBalancePayload.getCreditCardNumber());
            if (optionalCreditCard.isPresent()) {
                CreditCard creditCard = optionalCreditCard.get();
                creditCardsByNumber.put(updateBalancePayload.getCreditCardNumber(), creditCard);
            } else {
                return ResponseEntity.badRequest().build();
            }
        }

        for (UpdateBalancePayload updateBalancePayload : payload) {
            CreditCard creditCard = creditCardsByNumber.get(updateBalancePayload.getCreditCardNumber());
            BalanceHistory newBalanceHistory = new BalanceHistory();
            newBalanceHistory.setDate(updateBalancePayload.getTransactionTime());
            newBalanceHistory.setBalance(updateBalancePayload.getCurrentBalance());
            newBalanceHistory.setCreditCard(creditCard);
            creditCard.getBalanceHistory().add(0, newBalanceHistory);
        }


        creditCardRepository.saveAll(creditCardsByNumber.values());
        return ResponseEntity.ok().build();
    }

}
